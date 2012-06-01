MongoFile Plugin
================

The MongoFile plugin add methods to domain instances to save, retrieve and delete associated files from a MongoDB file store. The plugin can also write a file out to an HTTP response. This plugin depends on the MongoDB plugin.

Configuration
-------------

Once installed, append the following to DataSource.groovy:

```groovy
grails {
    mongo {
        databaseName = "db"
    }
}
```

CRUD Operations on Files
------------------------

Each domain class in the Grails application will now have a number of methods you can call to create, retrieve, update and delete files. Note that the fieldName parameter is optional and only needs to be used in the case of a two or more files needing to be stored on a domain class instance. It can be omitted entirely for the first file if desired.

```groovy
saveMongoFile(MultipartFile file, String fieldName = '')
```
Saves a file to the MongoDB store, associated with the domain instance, replacing any existing file for the domain instance (and fieldName if specified). See the section on [uploading files](http://grails.org/doc/2.0.x/guide/theWebLayer.html#uploadingFiles) in the Grails docs.

```groovy
saveMongoFile(byte[] fileContents, String fileName, String fieldName = '')
```
Saves file bytes to the MongoDB store, associated with the domain instance, replacing any existing file for the domain instance (and fieldName if specified). 

```groovy
saveMongoFile(InputStream inputStream, String fileName, String fieldName = '')
```
Saves stream bytes to the MongoDB store, associated with the domain instance, replacing any existing file for the domain instance (and fieldName if specified). 

```groovy
deleteMongoFile(String fieldName = '')
```
Deletes the associated file from the MongoDB store.

```groovy
getMongoFile(String fieldName = '')
```
Retrieves a [GridFSDBFile](http://api.mongodb.org/java/current/com/mongodb/gridfs/GridFSDBFile.html) object from the MongoDB store

```groovy
mongoFileExists(String fieldName = '')
```
Returns true or false depending on whether a file is associated with the domain instance. Useful to test whether to render a tag or not

Example
-------

GSP:
```html
<g:uploadForm action="save">
	<input type="file" name="logo" />
	<input type="submit" />
</g:uploadForm>
```

Controller:
```groovy
def save() {
    def userInstance = new User(params)
    def logo = request.getFile('logo')

	if (logo.empty) {
        flash.message = 'Logo must be uploaded'
        render(view: "create", model: [userInstance: userInstance])
        return
    }
    
    if (!userInstance.save(flush: true)) {
        render(view: "create", model: [userInstance: userInstance])
        return
    }

    userInstance.saveMongoFile(file)

	flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
    redirect(action: "show", id: userInstance.id)
}
```

MongoFile Taglib
----------------

A taglib is provided to call files from GSP pages. In Controllers and GSPs, you can get a link to a file by using, for example

```groovy
mongofile.createLinkTo(domainInstance: userInstance)
```

or alternatively without the instance (but with the optional fieldName and attachment parameters) by using 

```groovy
mongofile.createLinkTo(domainClass: 'User', id: 1, fieldName: 'icon', attachment: true)
```

However, it is more likely you will want to link to files stored as images or downloads. In the case of images, use for example

```html
<mongofile:img domainInstance="${userInstance}" fieldName="icon" />
```

To link to a file download, use the following. Note you can add extra attributes such as 'class'. If you omit the body text in the tag, as in the example below, the file name is retrieved from the MongoDB store and used for the link text. (Note that if you are using this with large numbers of big files on a page it may be more performant to store the filename in the domain class instead.)

```html
<mongofile:link domainInstance="${userInstance}" fieldName="pdf" class="download-pdf" />
```

It is often useful to test whether to render the tag first:

```html
<g:if test="${userInstance.mongoFileExists('icon')}"><mongofile:img domainInstance="${userInstance}" fieldName="icon" /></g:if>
```

Using the MongoFileService directly
-----------------------------------

Most of the time the methods above should be enough. However, you can inject the MongoFileService into a Controller or another Service with

```groovy
def mongoFileService
```

The following primary methods are available on MongoFileService:

```groovy
saveFile(MultipartFile file, Class domainClass, Long id, String fieldName = '')
```
Saves a file to the MongoDB store, associated with an domain object. See the section on [uploading files](http://grails.org/doc/2.0.x/guide/theWebLayer.html#uploadingFiles) in the Grails docs.

```groovy
saveFile(byte[] fileContents, String fileName, Class domainClass, Long id, String fieldName = '')
```
Saves file bytes to the MongoDB store, associated with an domain object. 

```groovy
saveFile(InputStream inputStream, String fileName, Class domainClass, Long id, String fieldName = '')
```
Saves stream bytes to the MongoDB store, associated with an domain object. 

```groovy
deleteFile(Class domainClass, Long id, String fieldName = '')
```
Deletes a associated file from the MongoDB store.

```groovy
getFile(Class domainClass, Long id, String fieldName = '')
```
Retrieves a [GridFSDBFile](http://api.mongodb.org/java/current/com/mongodb/gridfs/GridFSDBFile.html) object from the MongoDB store

```groovy
deliverFile(HttpServletResponse response, boolean asAttachment, Class domainClass, Long id, String fieldName = '')
```
Writes the associated file to the response, either directly or as an attachment. (Specifying attachment=true changes the response's Content-Disposition header.)

```groovy
dropCollections()
```
Use to drop all collections the database in order to start afresh. Use with caution! For example:

```groovy
import grails.util.GrailsUtil

class BootStrap {
    def mongoFileService

    def init = { servletContext ->
        switch(GrailsUtil.environment) {
            case "development":
                mongoFileService.dropCollections()
        }
    }
	....
}
```

```groovy
dropDatabase()
```
Use to drop the database entirely in order to start afresh. Again, use with caution!


Implementation note
-------------------

Each file is stored in a MongoDB collection (bucket), named after the domain class name and fieldname if present, joined with an underscore (${domain}_${fieldName}). On the mongo console you could list the stored files with 

```
db.user_icon.files.find({});
db.user_thumbnail.files.find({});
```
