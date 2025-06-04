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
  @Path("model")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public void postModel(Model model) {
    System.out.println("In the controller.");
    System.out.println(model);
    System.out.println(model.getFoo());
    System.out.println(model.getBar());
    System.out.println(model.getFizz());
    System.out.println(model.getBuzz());
    System.out.println(model.getFiles());
  }

  @POST
  @Path("enum")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public void postEnum(EnumWithConstructor derp) {
    System.out.println(derp);
  }
}
