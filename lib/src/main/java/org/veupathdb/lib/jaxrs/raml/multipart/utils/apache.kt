@file:Suppress("NOTHING_TO_INLINE")
package org.veupathdb.lib.jaxrs.raml.multipart.utils

import com.fasterxml.jackson.core.JsonParseException
import jakarta.ws.rs.BadRequestException
import org.apache.commons.fileupload.MultipartStream
import org.veupathdb.lib.jaxrs.raml.multipart.MultipartMessageBodyReader
import java.io.ByteArrayOutputStream

private val NewLineRGX = Regex("\r\n")
private val HeadSepRGX = Regex(": *")
private val PartSepRGX = Regex("; +")

private const val LCContentDisp  = "content-disposition"
private const val FormNamePrefix = "name="
private const val FileNamePrefix = "filename="

private const val InitialBufferSize = 8192


internal fun MultipartStream.contentToString(maxSize: Long) =
  ByteArrayOutputStream(InitialBufferSize).let {
    if (readBodyData(CappedOutputStream(maxSize, it)) == 0)
      ""
    else
      it.toByteArray().decodeToString()
  }


internal inline fun MultipartStream.parseHeaders() = readHeaders().parseHeaders()

internal fun String?.parseHeaders(): Map<String, List<String>> {
  if (this == null)
    return emptyMap()

  val out = HashMap<String, List<String>>(2)

  for (headerLine in split(NewLineRGX)) {
    // Sometimes empty lines creep in here, filter them out so that we don't 400
    if (headerLine.isBlank())
      continue

    if (headerLine.indexOf(':') < 1)
      throw BadRequestException("Malformed multipart/form-data body part header.")

    val parts = headerLine.split(HeadSepRGX, 2)
    out[parts[0]] = parts[1].split(PartSepRGX)
  }

  return out
}

internal fun MultipartStream.readContentAsJsonNode(maxSize: Long) =
  contentToString(maxSize).let {
    try {
      MultipartMessageBodyReader.mapper.readTree(it)
    } catch (_: JsonParseException) {
      MultipartMessageBodyReader.mapper.readTree("\"$it\"")
    }
  }

/**
 * Attempts to retrieve the form field name from the receiver header value list.
 *
 * @return The form name value as parsed from the headers or `null` if no form
 * name value was provided.
 */
internal inline fun List<String>.getFormName() =
  find { it.startsWith(FormNamePrefix) }
    ?.substring(FormNamePrefix.length)
    ?.removeQuotes()

/**
 * Attempts to retrieve the file name from the receiver header value list.
 *
 * @return The file name value as parsed from the headers or `null` if no file
 * name value was provided.
 */
internal inline fun List<String>.getFileName() =
  find { it.startsWith(FileNamePrefix) }
    ?.substring(FileNamePrefix.length)
    ?.removeQuotes()

/**
 * Removes a single pair of double quotes from the start and end of the receiver
 * string only if such a pair of quotes exists.
 *
 * @return The receiver string which may have had a single double quote
 * character removed from both the start and end of the string.
 */
internal inline fun String.removeQuotes() =
  if (get(0) == '"' && get(lastIndex) == '"')
    substring(1, lastIndex)
  else
    this

/**
 * Retrieves the `Content-Disposition` header from the receiver map, if such an
 * entry exists.
 *
 * @return The values of the `Content-Disposition` header, if it was set,
 * otherwise `null`.
 */
internal inline fun Map<String, List<String>>.getContentDisposition() =
  get(keys.find { it.lowercase() == LCContentDisp })

internal inline fun Map<String, List<String>>.requireContentDisposition() =
  getContentDisposition() ?: throw BadRequestException("Multipart section missing Content-Disposition header.")
