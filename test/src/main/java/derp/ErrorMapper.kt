package derp

import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper

class ErrorMapper : ExceptionMapper<Throwable> {

  override fun toResponse(exception: Throwable): Response {
    val code = when (exception) {
      is BadRequestException -> 400
      else  -> 500
    }

    exception.printStackTrace()

    return Response.status(code).entity(exception.message).build()
  }
}