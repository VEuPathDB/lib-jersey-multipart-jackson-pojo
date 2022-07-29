package derp;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.File;

@Path("/")
public class Controller {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String get() {
    return "Goodbye cruel world.";
  }

  // TODO: When the input parameter is of type file, the multipart filter isn't
  //       used.  Instead some default jersey serializer is used that copies the
  //       full multipart body into the file.
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public void post(File model) {
    System.out.println("In the controller.");
    System.out.println(model);
  }
}
