package org.veupathdb.lib.jaxrs.raml.multipart.utils

import jakarta.ws.rs.BadRequestException
import java.io.OutputStream

/**
 * Capped Size Output Stream
 *
 * A simple output stream wrapper that throws an exception if more than the
 * specified max number of bytes is read.
 */
class CappedOutputStream(
  private val maxBytes: Int,
  private val stream: OutputStream
) : OutputStream() {
  private var written = 0

  override fun write(b: Int) {
    if (++written > maxBytes)
      throw BadRequestException("Form field exceeded max number of bytes: $maxBytes")

    stream.write(b)
  }
}