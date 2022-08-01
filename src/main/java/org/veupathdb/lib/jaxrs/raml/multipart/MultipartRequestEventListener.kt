package org.veupathdb.lib.jaxrs.raml.multipart

import org.glassfish.jersey.server.monitoring.RequestEvent
import org.glassfish.jersey.server.monitoring.RequestEventListener
import org.veupathdb.lib.jaxrs.raml.multipart.utils.createTempDirectory
import org.veupathdb.lib.jaxrs.raml.multipart.utils.deleteTempDirectory
import org.veupathdb.lib.jaxrs.raml.multipart.utils.isMultipart
import java.io.File

/**
 * Multipart Request Event Listener
 *
 * Jersey request event listener specifically meant for performing cleanup after
 * request completion.
 *
 * This listener does not care if the completion status was success or failure.
 *
 * Request events before the request completion event are ignored.
 *
 * On receipt of the request completion event, this listener will attempt to
 * remove a temp directory created for a `multipart/form-data` request which
 * contained file uploads.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 1.0.0
 */
class MultipartRequestEventListener : RequestEventListener {

  /**
   * Method called on request event.
   *
   * This method does nothing unless the given event is a `FINISHED` event which
   * signifies the Jersey request processing has completed.
   *
   * If the given event _is_ a `FINISHED` event, this method will attempt to
   * remove a temp directory which will have been associated with the request
   * for `multipart/form-data` requests containing file uploads.
   *
   * If the request does not have a temp directory associated with it, this
   * method does nothing.
   *
   * @param event Request event.
   */
  override fun onEvent(event: RequestEvent) {
    if (event.type == RequestEvent.Type.START)
      onRequestStart(event)
    if (event.type == RequestEvent.Type.FINISHED)
      onRequestEnd(event)
  }

  private fun onRequestStart(event: RequestEvent) {
    if (event.containerRequest.isMultipart())
      event.containerRequest.headers.add(TempDirHeader, event.containerRequest.createTempDirectory().path)
  }

  private fun onRequestEnd(event: RequestEvent) {
    if (event.containerRequest.isMultipart())
      event.containerRequest.deleteTempDirectory()
  }
}