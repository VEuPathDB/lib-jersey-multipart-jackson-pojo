package derp;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SomeEnum {
  SomeValue("some value");

  public final String value;

  SomeEnum(String value) {
    this.value = value;
  }

  @JsonCreator
  public static SomeEnum fromString(String value) {
    for (SomeEnum val : values())
      if (val.value.equals(value))
        return val;

    throw new IllegalArgumentException("Unrecognized enum value: " + value);
  }
}
