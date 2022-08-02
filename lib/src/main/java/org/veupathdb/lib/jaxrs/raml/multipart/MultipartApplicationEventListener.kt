package org.veupathdb.lib.jaxrs.raml.multipart

import jakarta.ws.rs.ext.Provider
import org.glassfish.jersey.server.monitoring.ApplicationEvent
import org.glassfish.jersey.server.monitoring.ApplicationEventListener
import org.glassfish.jersey.server.monitoring.RequestEvent
import org.veupathdb.lib.jaxrs.raml.multipart.utils.isMultipart

/**
 * Application level event listener for the multipart plugin.
 *
 * This class must be registered with the container resources.
 *
 * This listener provides [MultipartRequestEventListener] instances for requests
 * that are sent in with the `multipart/form-data` Content-Type header set.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 1.0.0
 */
@Provider
class MultipartApplicationEventListener : ApplicationEventListener {

  /**
   * Does nothing.
   */
  override fun onEvent(event: ApplicationEvent) {}

  /**
   * Returns a new [MultipartRequestEventListener] if the incoming request has
   * a `multipart/form-data` Content-Type header set, otherwise returns `null`.
   *
   * @param requestEvent Request start event.
   *
   * @return Request event listener or `null`.
   */
  override fun onRequest(requestEvent: RequestEvent) =
    if (requestEvent.isMultipart())
      MultipartRequestEventListener(requestEvent)
    else
      null
}