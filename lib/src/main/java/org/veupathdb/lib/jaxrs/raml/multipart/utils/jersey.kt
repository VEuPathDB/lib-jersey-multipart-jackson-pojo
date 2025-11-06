@file:Suppress("NOTHING_TO_INLINE")
package org.veupathdb.lib.jaxrs.raml.multipart.utils

import jakarta.ws.rs.core.MediaType
import org.glassfish.jersey.server.ContainerRequest
import org.glassfish.jersey.server.monitoring.RequestEvent


/**
 * Tests whether the receiver `RequestEvent` is for a `multipart/form-data`
 * request.
 *
 * @receiver Event to test.
 *
 * @return `true` if the request is a `multipart/form-data` request, otherwise
 * `false`.
 */
internal inline fun RequestEvent.isMultipart() =
  containerRequest.isMultipart()

/**
 * Tests whether the receiver `ContainerRequest` is for a `multipart/form-data`
 * request.
 *
 * @receiver Request to test.
 *
 * @return `true` if the request is a `multipart/form-data` request, otherwise
 * `false`.
 */
internal inline fun ContainerRequest.isMultipart() =
  mediaType?.isCompatible(MediaType.MULTIPART_FORM_DATA_TYPE) ?: false