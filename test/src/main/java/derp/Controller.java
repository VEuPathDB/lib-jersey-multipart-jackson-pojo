package derp;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class Controller {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String get() {
    return "Goodbye cruel world.";
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public void post(Model model) {
    System.out.println("In the controller.");
    System.out.println(model);
    System.out.println(model.getFoo());
    System.out.println(model.getBar());
    System.out.println(model.getFizz());
  }
}
