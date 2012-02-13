Mongofile Plugin
================

The Mongofile plugin provides a FileService that saves, retrieves and deletes files from a MongoDB file store, associated with a domain object. The plugin can also write a file out to an HTTP response.

The following primary methods are available on FileService:

```groovy
saveFile(Class domainClass, Long id, CommonsMultipartFile file)
```
Saves a file to the MongoDB store, associated with an domain object. See the section on [uploading files](http://grails.org/doc/2.0.x/guide/theWebLayer.html#uploadingFiles) in the Grails docs.

```groovy
deleteFile(Class domainClass, Long id)
```
Deletes a associated file from the MongoDB store.

```groovy
getFile(Class domainClass, Long id)
```
Retrieves a [GridFSDBFile](http://api.mongodb.org/java/current/com/mongodb/gridfs/GridFSDBFile.html) object from the MongoDB store

```groovy
deliverFile(HttpServletResponse response, Class domainClass, Long id, boolean asAttachment)
```
Writes the associated file to the response, either directly or as an attachment.

```groovy
dropDatabase()
```
Use in Bootstrap.groovy in dev and test environments to drop the database in order to start afresh. Use with caution! For example:

```groovy
import grails.util.GrailsUtil

class BootStrap {
    def fileService

    def init = { servletContext ->
        switch(GrailsUtil.environment) {
            case "development":
                fileService.dropDatabase()
        }
    }
	....
}
```

Implementation note: Each file is stored in a MongoDB collection (bucket), named after the domain class name. 