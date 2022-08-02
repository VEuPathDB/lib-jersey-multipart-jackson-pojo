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
  private SomeEnum fizz;

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
  public SomeEnum getFizz() {
    return fizz;
  }

  @JsonSetter("fizz")
  public void setFizz(SomeEnum fizz) {
    this.fizz = fizz;
  }
}
