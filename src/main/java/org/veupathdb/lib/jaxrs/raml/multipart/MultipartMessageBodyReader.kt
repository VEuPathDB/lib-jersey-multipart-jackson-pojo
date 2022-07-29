package org.veupathdb.lib.jaxrs.raml.multipart

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyReader
import org.apache.commons.fileupload.MultipartStream
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.jaxrs.raml.multipart.utils.*
import java.io.File
import java.io.InputStream
import java.lang.reflect.Type
import java.util.UUID

private const val DefaultFileName = "upload"

private const val BufferSize = 8192

////////////////////////////////////////////////////////////////////////////////
//
//   TODOS!!
//
// TODO: Tests:
//       - Confirm that file uploads are decoded from base64 by the apache class
//         or implement/use a streaming base64 decode to get the raw value on
//         pipe to output stream.
//       - Test file upload to non-file POJO property




class MultipartMessageBodyReader(
  @Context
  private val request: ContainerRequest
) : MessageBodyReader<Any> {

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
    println("in the filter")

    // Open a stream over the contents of the multipart/form-data body.
    val stream = MultipartStream(entityStream, mediaType.requireBoundaryBytes(), BufferSize, null)

    // If the method just takes a raw file as an input:
    if (File::class.java.isAssignableFrom(type))
      return fileReadFrom(stream)
        .also { entityStream.close() }

    // If the method takes an Enum type as an input:
    if (type.isEnum)
      return enumReadFrom(type, stream)
        .also { entityStream.close() }

    // TODO: Map and Collection?

    // Else, assume the type is a POJO and deserialize accordingly:
    return pojoReadFrom(type, stream)
  }

  /**
   * Reads the contents of the first segment of the input body into a file then
   * returns that file.
   *
   *
   */
  private fun fileReadFrom(stream: MultipartStream): File {
    // Skip over any extra stuff to get to the first content section.
    stream.skipPreamble()
      || throw BadRequestException("Missing body content.")

    // Get the content disposition information from the section headers.
    val cont = stream.parseHeaders()
      .requireContentDisposition()

    // Get the file name or provide a name for the file if no filename was sent
    // in with the request.
    val fileName = cont.getFileName() ?: cont.getFormName() ?: DefaultFileName

    // Create a temp directory to hold the uploaded file.
    //
    // We do it this way so the file can keep its original name since the name
    // of the returned file is the only way for us to communicate the original
    // file name to the controller method.
    val tmpDir = initTmpDir()

    // TODO: mark the tmpDir to be removed somehow

    // Create the upload file and populate it with the contents of the stream.
    return File(tmpDir, fileName).apply {
      createNewFile()
      outputStream().use { stream.readBodyData(it) }
    }
  }

  /**
   * Attempts to parse the contents of the first segment of the input body as
   * the type expected by the static constructor of the given enum [type].
   *
   * The given enum [type] must have a static constructor annotated with the
   * Jackson `@JsonCreator` annotation.
   *
   * If no such annotated static constructor method is found on the type this
   * method will throw a 500 exception.
   *
   * @param type Class for the enum type that should be instantiated.
   *
   * @param stream Incoming request body.
   *
   * @return A new instance of the given enum [type] parsed from the first
   * segment of the input body.
   */
  private fun enumReadFrom(type: Class<Any>, stream: MultipartStream): Any {
    TODO("fuck.  we need to find the @JsonCreator static method on the enum" +
      " class, figure out what type of input it takes, then try and convert" +
      " the raw text we were given into the expected type.")
  }

  private fun pojoReadFrom(type: Class<Any>, stream: MultipartStream): Any {
    // If the method takes something other than a file, then assume it's a pojo
    // and attempt to deserialize into a map that will be converted into that
    // pojo type.
    //
    // NOTE: We don't try and deserialize into the pojo itself because it may be
    // an interface, an enum, or a pojo with zero no-arg constructors.

    // TODO: does the apache stream decode the base64 wrapping the raw file
    //       content?


    val temp = HashMap<String, Any>()

    if (!stream.skipPreamble())
      return mapper.convertValue(temp, type)

    val fields = type.fieldsMap()
    val tmpDir = initTmpDir()

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
        temp[fieldName] = stream.readContentAsJsonNode()
      }

    } while (stream.readBoundary())

    // Iterate through parts in the multipart input
    // for each part
    //   map the part to a field on the POJO type
    //   if the pojo type is type file, pipe part to temp file and assign prop
    //   else parse pojo inline and assign prop
    return mapper.convertValue(temp, type)
  }


  /**
   * Creates a new temporary directory for this request, appends it to the
   * request as a property, then returns it.
   *
   * The created temp directory is expected to be cleaned up by
   * [MultipartRequestEventListener] on request completion.
   *
   * @return The newly created temp directory.
   */
  private fun initTmpDir(): File {
    // Create a new temp directory with a name that should not conflict with
    // anything else.
    val out = File(tempDirectory, UUID.randomUUID().toString())
      .also { it.mkdir() }

    // Attach the new directory to the request, so we can clean it up later.
    request.setProperty(TempDirProperty, out)

    // Return the new temp directory.
    return out
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
    var mapper = ObjectMapper()

    var tempDirectory = File("/tmp")
      set(value) {
        if (!value.exists())
          throw IllegalStateException("Cannot use temp directory that does not exist.")

        if (!value.isDirectory)
          throw IllegalStateException("Temp path must be a directory.")

        field = value
      }
  }
}
