package derp;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.File;

public class Model {
  @JsonProperty("foo")
  private String foo;

  @JsonProperty("bar")
  private File bar;

  @JsonProperty("fizz")
  private EnumWithConstructor fizz;

  @JsonProperty("buzz")
  private EnumWithAnnotations buzz;

  @JsonGetter("foo")
  public String getFoo() {
    return foo;
  }

  @JsonSetter("foo")
  public void setFoo(String foo) {
    this.foo = foo;
  }

  @JsonGetter("bar")
  public File getBar() {
    return bar;
  }

  @JsonSetter("bar")
  public void setBar(File bar) {
    this.bar = bar;
  }

  @JsonGetter("fizz")
  public EnumWithConstructor getFizz() {
    return fizz;
  }

  @JsonSetter("fizz")
  public void setFizz(EnumWithConstructor fizz) {
    this.fizz = fizz;
  }

  @JsonGetter("buzz")
  public EnumWithAnnotations getBuzz() { return buzz; }

  @JsonSetter("buzz")
  public void setBuzz(EnumWithAnnotations buzz) { this.buzz = buzz; }
}
