package derp;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EnumWithAnnotations {

  @JsonProperty("foo")
  SomeEnumValue1,

  @JsonProperty("bar")
  SomeEnumValue2,

  @JsonProperty("fizz")
  SomeEnumValue3,

  @JsonProperty("buzz")
  SomeEnumValue4
}
