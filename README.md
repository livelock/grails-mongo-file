Mongofile Plugin
================

The Mongofile plugin provides a FileService that saves, retrieves and deletes files from a MongoDB file store, associated with a domain object. The plugin can also write a file out to an HTTP response. Once installed, append the following to DataSource.groovy:

```groovy
grails {
    mongo {
        databaseName = "db"
    }
}
```

You can inject the FileService into a Controller or another Service with

```groovy
def fileService
```

The following primary methods are available on FileService:

```groovy
saveFile(CommonsMultipartFile file, Class domainClass, Long id)
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
deliverFile(HttpServletResponse response, boolean asAttachment, Class domainClass, Long id)
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

Should a domain object need multiple files stored, you can append an additional String parameter to any of the methods above to indicate a field name, for example:

```groovy
saveFile(file, User, 1, 'icon')
saveFile(thumbFile, User, 1, 'thumbnail')
```

Implementation note: Each file is stored in a MongoDB collection (bucket), named after the domain class name and fieldname if present, joined with an underscore (${domain}_${fieldName}). On the mongo console you could list the stored files with 

```
db.user_icon.files.find({});
db.user_thumbnail.files.find({});
```
