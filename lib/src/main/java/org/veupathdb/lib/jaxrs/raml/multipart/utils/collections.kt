package org.veupathdb.lib.jaxrs.raml.multipart.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.WildcardType

typealias ColConstructor<T> = (Int) -> MutableCollection<T>

fun ParameterizedType.decomposeCollection(): Pair<Class<*>, Class<*>>? {
  if (rawType !is Class<*>)
    return null

  val colType = when {
    List::class.java.isAssignableFrom(rawType as Class<*>) -> List::class.java
    Set::class.java.isAssignableFrom(rawType as Class<*>)  -> Set::class.java
    else -> return null
  }

  if (actualTypeArguments.size != 1)
    return null

  return when (val c = actualTypeArguments[0]) {
    is Class<*>     -> colType to c
    is WildcardType -> c.decompose()?.let { colType to it }
    else            -> null
  }
}

fun WildcardType.decompose() =
  if (upperBounds.size == 1)
    upperBounds[0] as? Class<*>
  else
    null
