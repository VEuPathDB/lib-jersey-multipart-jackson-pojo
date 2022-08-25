package derp;

import org.glassfish.jersey.server.ResourceConfig;
import org.veupathdb.lib.jaxrs.raml.multipart.MultipartApplicationEventListener;
import org.veupathdb.lib.jaxrs.raml.multipart.MultipartMessageBodyReader;

public class Resources extends ResourceConfig {
  public Resources() {
    property("jersey.config.server.tracing.type", "ALL");
    property("jersey.config.server.tracing.threshold", "VERBOSE");

    registerClasses(
      MultipartApplicationEventListener.class,

      ErrorMapper.class,

      MultipartMessageBodyReader.class,
      Controller.class
    );
  }
}
