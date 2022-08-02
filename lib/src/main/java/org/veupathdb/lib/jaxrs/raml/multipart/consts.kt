package org.veupathdb.lib.jaxrs.raml.multipart

/**
 * Extra header injected to pass information about the multipart/form-data
 * request's attached temp directory through to the `MessageBodyReader`.
 *
 * This is done because `MessageBodyReader` instances must be singletons and
 * cannot have request context information injected into them.  This means that
 * they cannot have access to a `ContainerRequest` instance and thus cannot
 * access the custom properties on that instance (where the temp directory
 * reference is stored).
 */
internal const val TempDirHeader = "X-Multipart-Form-Data-Temp-Directory"