package org.veupathdb.lib.jaxrs.raml.multipart

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.InternalServerErrorException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyReader
import org.apache.commons.fileupload.MultipartStream
import org.veupathdb.lib.jaxrs.raml.multipart.utils.*
import java.io.File
import java.io.InputStream
import java.lang.reflect.Type

private const val DefaultFileName = "upload"

private const val BufferSize = 8192

class MultipartMessageBodyReader : MessageBodyReader<Any> {

  /**
   * Tests whether this `MessageBodyReader` should kick in for the given
   * request.
   *
   * @return `true` if the input request is of type `multipart/form-data`,
   * otherwise `false`.
   */
  override fun isReadable(
    type: Class<*>,
    genericType: Type,
    annotations: Array<out Annotation>,
    mediaType: MediaType,
  ) = mediaType.isCompatible(MediaType.MULTIPART_FORM_DATA_TYPE)

  override fun readFrom(
    type: Class<Any>,
    genericType: Type,
    annotations: Array<out Annotation>,
    mediaType: MediaType,
    httpHeaders: MultivaluedMap<String, String>,
    entityStream: InputStream,
  ): Any {
    // Open a stream over the contents of the multipart/form-data body.
    val stream = MultipartStream(entityStream, mediaType.requireBoundaryBytes(), BufferSize, null)

    // Get hold of the temp directory assigned to this multipart request.
    val tmpDir = httpHeaders.getTempDirectory()
      ?: throw InternalServerErrorException("Multipart request made it to the MessageBodyReader with no temp directory")

    // If the method just takes a raw file as an input:
    if (File::class.java.isAssignableFrom(type))
      return fileReadFrom(stream, tmpDir)
        .also { entityStream.close() }

    // If the method takes an Enum type as an input:
    if (type.isEnum)
      return enumReadFrom(type, stream)
        .also { entityStream.close() }

    // TODO: Map and Collection?

    // Else, assume the type is a POJO and deserialize accordingly:
    return pojoReadFrom(type, stream, tmpDir)
  }

  /**
   * Reads the contents of the first segment of the input body into a file then
   * returns that file.
   */
  private fun fileReadFrom(stream: MultipartStream, tmpDir: File): File {
    // Skip over any extra stuff to get to the first content section.
    stream.skipPreamble()
      || throw BadRequestException("Missing body content.")

    // Get the content disposition information from the section headers.
    val cont = stream.parseHeaders()
      .requireContentDisposition()

    // Get the file name or provide a name for the file if no filename was sent
    // in with the request.
    val fileName = cont.getFileName() ?: cont.getFormName() ?: DefaultFileName

    // Create the upload file and populate it with the contents of the stream.
    return File(tmpDir, fileName).apply {
      createNewFile()
      outputStream().use { stream.readBodyData(it) }
    }
  }

  /**
   * Attempts to parse the contents of the first segment of the input body as
   * the type expected by the static "constructor" of the given enum [type].
   *
   * The given enum [type] must have a static "constructor" annotated with the
   * Jackson `@JsonCreator` annotation.
   *
   * If no such annotated static method is found on the type this method will
   * throw a 500 exception.
   *
   * @param type Class for the enum type that should be instantiated.
   *
   * @param stream Incoming request body.
   *
   * @return A new instance of the given enum [type] parsed from the first
   * segment of the input body.
   */
  private fun enumReadFrom(type: Class<Any>, stream: MultipartStream): Any {
    // Locate the static method that will give us an instance of the target
    // enum.
    //
    // This method will be annotated with the jackson `@JsonCreator` annotation.
    //
    // If no such method exists, throw a 500 exception as the server source code
    // is invalid.
    val constructor = type.getJacksonConstructor()
      ?: throw InternalServerErrorException("Cannot construct instance of $type from multipart/form-data input.")

    // Skip over the prefix information, headers, etc...
    stream.skipPreamble()
      || throw BadRequestException("Missing body content.")

    // Attempt to read the first part of the body as the generic type defined by
    // the constructor method's input parameter.
    val inp = mapper.readValue<Any>(stream.contentToString(maxVariableSize),
      mapper.typeFactory.constructType(constructor.genericParameterTypes[0]))

    // Return the result of calling the target method.
    return constructor.invoke(null, inp)
  }

  private fun pojoReadFrom(type: Class<Any>, stream: MultipartStream, tmpDir: File): Any {
    // NOTE: We don't try and deserialize into the pojo itself because it may be
    // an interface, an enum, or a pojo with zero no-arg constructors.
    val temp = HashMap<String, Any>()

    // Skip over the initial header info that is not needed for our parsing.\
    //
    // If this method returns false, then there is nothing in the body, so we
    // can attempt to convert our empty map to the pojo now and bail.
    if (!stream.skipPreamble())
      return mapper.convertValue(temp, type)

    // Get a map of the fields on the POJO and their types.
    val fields = type.fieldsMap()

    do {
      val headers     = stream.parseHeaders()
      val contentDisp = headers.getContentDisposition()
        ?: throw BadRequestException("Bad multipart section, missing Content-Disposition header.")

      val fieldName = contentDisp.getFormName()
        ?: throw BadRequestException("Bad multipart section, no form field name provided.")

      // Match the field name to a field on the conversion type.
      //
      // If a match IS found AND the type of the field IS File, write the
      // contents of this form-data section to file and assign the file ref to
      // the temp map.
      //
      // If a match IS found AND the type of the field IS NOT File, attempt to
      // convert the value to the target type.
      //
      //     If the target type is primitive, attempt the conversion ourselves,
      //     if the target type is complex, attempt to parse the value as JSON
      //     and put a JsonNode instance into the temp map.
      //
      // If a match IS NOT found, attempt to wrap the value as JSON and put a
      // JsonNode into the temp map, if that is not possible put the value in
      // the map as a string.

      if (fields[fieldName] == File::class.java) {
        val fileName = contentDisp.getFileName()
          ?: fieldName

        val file = File(tmpDir, fileName).apply {
          createNewFile()
          outputStream().use { stream.readBodyData(it) }
        }

        temp[fieldName] = file

      } else {
        // Do regular parse
        temp[fieldName] = stream.readContentAsJsonNode(maxVariableSize)
      }

    } while (stream.readBoundary())

    // Iterate through parts in the multipart input
    // for each part
    //   map the part to a field on the POJO type
    //   if the pojo type is type file, pipe part to temp file and assign prop
    //   else parse pojo inline and assign prop
    return mapper.convertValue(temp, type)
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun MediaType.requireBoundary() = parameters["boundary"]
    ?: throw BadRequestException("Content-Type header missing boundary string.")

  @Suppress("NOTHING_TO_INLINE")
  private inline fun MediaType.requireBoundaryBytes() = requireBoundary().toByteArray()

  companion object {

    // Jackson
    // TODO: make this static and expose it so it can be configured with extras
    //       as needed by the specific service.  Consider using the jackson
    //       singleton library.
    @JvmStatic
    var mapper = ObjectMapper()

    /**
     * Max size for a single non-file field that will be read into memory as
     * part of a POJO.
     *
     * Defaults to 16MiB.
     */
    @JvmStatic
    var maxVariableSize = 16_777_216
  }
}
