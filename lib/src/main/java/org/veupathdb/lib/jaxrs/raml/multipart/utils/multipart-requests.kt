@file:JvmName("MultipartRequests")

package org.veupathdb.lib.jaxrs.raml.multipart.utils

import jakarta.ws.rs.InternalServerErrorException
import jakarta.ws.rs.core.MultivaluedMap
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.jaxrs.raml.multipart.TempDirHeader
import java.io.File
import java.util.UUID

internal const val TempDirProperty = "temp-dir-path"

var tempDirLocation: File = File("/tmp")

var tempDirPrefix: String = "multipart_"

fun MultivaluedMap<String, String>.getTempDirectory() =
  get(TempDirHeader)?.get(0)?.let { File(it) }

fun ContainerRequest.getTempDirectory() =
  getProperty(TempDirProperty) as File

fun ContainerRequest.deleteTempDirectory() =
  getTempDirectory().deleteRecursively()

internal fun ContainerRequest.createTempDirectory(): File {
  try {
    val tmpDir = File(tempDirLocation, tempDirPrefix + UUID.randomUUID().toString())

    if (!tmpDir.mkdir())
      throw IllegalStateException("temp directory with path $tmpDir was not created or already exists")

    setProperty(TempDirProperty, tmpDir)

    return tmpDir
  } catch (e: Exception) {
    throw InternalServerErrorException("Failed to create temporary directory for multipart/form-data request.", e)
  }
}