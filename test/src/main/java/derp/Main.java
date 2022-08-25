package derp;

import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;

public class Main  {

  public static void main(String[] args) throws IOException {
    HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
      UriBuilder.fromUri("//0.0.0.0").port(8080).build(),
      new Resources()
    );

    server.start();
  }

}
