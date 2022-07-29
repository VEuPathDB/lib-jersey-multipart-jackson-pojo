package org.veupathdb.lib.jaxrs.raml.multipart.utils

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import java.lang.reflect.Method

internal fun Class<Any>.fieldsMap(): Map<String, Class<*>> {
  val out = HashMap<String, Class<*>>(1)

  // Look through all the methods on the class for our targets
  for (method in methods) {
    // If the method doesn't take a single parameter it's not a plain setter,
    // and we can ignore it.
    if (method.parameterCount != 1)

    // If the method isn't a setter, ignore it.
      if (!method.name.startsWith("set"))
        continue

    // Get the jackson annotated field name (or ignore the field if it has
    // no such name).
    out.put(method.getJacksonName() ?: continue, method.parameters[0].type)
  }

  return out
}


private fun Method.getJacksonName(): String? {
  for (ann in annotations) {
    if (ann is JsonSetter)
      return ann.value
    if (ann is JsonProperty)
      return ann.value
  }

  return null
}
