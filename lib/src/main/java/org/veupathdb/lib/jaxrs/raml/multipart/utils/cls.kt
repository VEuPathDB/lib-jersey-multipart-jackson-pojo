package org.veupathdb.lib.jaxrs.raml.multipart.utils

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Type

internal fun Class<Any>.fieldsMap(): Map<String, Type> {
  val out = HashMap<String, Type>(1)

  // Look through all the methods on the class for our targets
  for (method in methods) {
    if (method.isStatic())
      continue

    // If the method doesn't take a single parameter it's not a plain setter,
    // and we can ignore it.
    if (method.parameterCount != 1)
      continue

    // If the method isn't a setter, ignore it.
    if (!method.name.startsWith("set"))
      continue

    // Get the jackson annotated field name (or ignore the field if it has
    // no such name).
    out[method.getJacksonName() ?: continue] = method.parameters[0].parameterizedType
  }

  return out
}

internal fun Class<Any>.getJacksonConstructor(): Method? {

  for (method in methods) {
    // Sift out any non-static methods
    if (!method.isStatic())
      continue

    // Sift out any methods that don't take exactly one parameter
    if (method.parameterCount != 1)
      continue

    // Sift out any methods that don't have the JsonCreator annotation
    if (!method.hasJacksonCreatorAnnotation())
      continue

    // Find the first method that returns a type compatible with this type.
    if (isAssignableFrom(method.returnType))
      return method
  }

  return null
}

internal fun Class<Any>.getJacksonAnnotatedEnumProperties(): Map<String, Any> {
  val out = HashMap<String, Any>(enumConstants.size)

  for (field in declaredFields) {
    if (!field.isEnumConstant)
      continue

    for (ann in field.annotations) {
      if (ann is JsonProperty) {
        out[ann.value] = field
      }
    }
  }

  return out
}

private fun Method.isStatic() = Modifier.isStatic(modifiers)

private fun Method.getJacksonName(): String? {
  for (ann in annotations) {
    if (ann is JsonSetter)
      return ann.value
    if (ann is JsonProperty)
      return ann.value
  }

  return null
}

private fun Method.hasJacksonCreatorAnnotation(): Boolean {
  for (annotation in annotations) {
    if (annotation is JsonCreator)
      return true
  }

  return false
}