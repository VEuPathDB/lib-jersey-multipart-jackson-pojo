package org.veupathdb.lib.jaxrs.raml.multipart

import org.glassfish.jersey.server.monitoring.RequestEvent
import org.glassfish.jersey.server.monitoring.RequestEventListener
import org.veupathdb.lib.jaxrs.raml.multipart.utils.createTempDirectory
import org.veupathdb.lib.jaxrs.raml.multipart.utils.deleteTempDirectory
import org.veupathdb.lib.jaxrs.raml.multipart.utils.isMultipart

/**
 * Multipart Request Event Listener
 *
 * Jersey request event listener that assigns a temporary directory to a request
 * on request start, then removes that directory on request finish.
 *
 * On creation of this event listener, a temp directory will be created for the
 * request that this listener is attached to.  On request completion, this
 * listener will remove the created temp directory and any of it's remaining
 * contents.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 1.0.0
 */
class MultipartRequestEventListener(event: RequestEvent) : RequestEventListener {

  // We do this on init because the `START` request event is never actually
  // passed to the `onEvent` method, the creation of the event listener _is_ the
  // `START` event notification.
  init {
    onRequestStart(event)
  }

  /**
   * Method called on request event.
   *
   * This method does nothing unless the given event is a `FINISHED` event which
   * signifies the Jersey request processing has completed.
   *
   * If the given event _is_ a `FINISHED` event, this method will attempt to
   * remove the temp directory associated with the request by this event
   * listener.
   *
   * @param event Request event.
   */
  override fun onEvent(event: RequestEvent) {
    if (event.type == RequestEvent.Type.FINISHED)
      onRequestEnd(event)
  }

  private fun onRequestStart(event: RequestEvent) {
    if (event.isMultipart())
      event.containerRequest.headers.add(TempDirHeader, event.containerRequest.createTempDirectory().path)
  }

  private fun onRequestEnd(event: RequestEvent) {
    if (event.isMultipart())
      event.containerRequest.deleteTempDirectory()
  }
}