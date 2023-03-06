package org.veupathdb.lib.jaxrs.raml.multipart

/**
 * JaxRS Multipart Upload Global Configuration
 */
object JaxRSMultipartUpload {

  /**
   * Maximum allowed size for a file upload (in bytes).
   *
   * Defaults to 3GiB.
   */
  @JvmStatic
  var maxFileUploadSize = 3_221_225_472L

  /**
   * Maximum allowed size for an in-memory field, class, or property (in bytes).
   *
   * Defaults to 16MiB.
   */
  @JvmStatic
  var maxInMemoryFieldSize = 16_777_216L
}