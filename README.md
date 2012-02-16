Mongofile Plugin
================

The Mongofile plugin add methods to domain instances to save, retrieve and delete associated files from a MongoDB file store. The plugin can also write a file out to an HTTP response. 

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
saveMongoFile(CommonsMultipartFile file, String fieldName = '')
```
Saves a file to the MongoDB store, associated with the domain instance. See the section on [uploading files](http://grails.org/doc/2.0.x/guide/theWebLayer.html#uploadingFiles) in the Grails docs.

```groovy
deleteMongofile(String fieldName = '')
```
Deletes the associated file from the MongoDB store.

```groovy
getMongofile(String fieldName = '')
```
Retrieves a [GridFSDBFile](http://api.mongodb.org/java/current/com/mongodb/gridfs/GridFSDBFile.html) object from the MongoDB store

```groovy
mongofileExists(String fieldName = '')
```
Returns true or false depending on whether a file is associated with the domain instance. Useful to test whether to render a tag or not

Example
-------

```groovy
def save() {
    def userInstance = new User(params)
    def logo = request.getFile('logo') // Make sure you use <g:uploadForm> with <input type="file" name="logo" />

	if (logo.empty) {
        flash.message = 'Logo must be uploaded'
        render(view: "create", model: [userInstance: userInstance])
        return
    }
    
    if (!userInstance.save(flush: true)) {
        render(view: "create", model: [userInstance: userInstance])
        return
    }

    userInstance.saveMongofile(file)

	flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
    redirect(action: "show", id: userInstance.id)
}
```

Mongofile Taglib
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

To link to a file download, use the following. Note you can add extra attributes such as 'class' below:

```html
<mongofile:link domainInstance="${userInstance}" fieldName="pdf" class="download-pdf" />
```

It is often useful to test whether to render the tag first:

```html
<g:if test="${userInstance.mongofileExists('icon')}"><mongofile:img domainInstance="${userInstance}" fieldName="icon" /></g:if>
```

Using the MongofileService directly
-----------------------------------

Most of the time the methods above should be enough. However, you can inject the MongofileService into a Controller or another Service with

```groovy
def mongofileService
```

The following primary methods are available on MongofileService:

```groovy
saveFile(CommonsMultipartFile file, Class domainClass, Long id, String fieldName = '')
```
Saves a file to the MongoDB store, associated with an domain object. See the section on [uploading files](http://grails.org/doc/2.0.x/guide/theWebLayer.html#uploadingFiles) in the Grails docs.

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
dropDatabase()
```
Use in Bootstrap.groovy in dev and test environments to drop the database in order to start afresh. Use with caution! For example:

```groovy
import grails.util.GrailsUtil

class BootStrap {
    def mongofileService

    def init = { servletContext ->
        switch(GrailsUtil.environment) {
            case "development":
                mongofileService.dropDatabase()
        }
    }
	....
}
```

Implementation note
-------------------

Each file is stored in a MongoDB collection (bucket), named after the domain class name and fieldname if present, joined with an underscore (${domain}_${fieldName}). On the mongo console you could list the stored files with 

```
db.user_icon.files.find({});
db.user_thumbnail.files.find({});
```
