package derp;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EnumWithConstructor {
  SomeValue("some value");

  public final String value;

  EnumWithConstructor(String value) {
    this.value = value;
  }

  @JsonCreator
  public static EnumWithConstructor fromString(String value) {
    for (EnumWithConstructor val : values())
      if (val.value.equals(value))
        return val;

    throw new IllegalArgumentException("Unrecognized enum value: " + value);
  }
}
