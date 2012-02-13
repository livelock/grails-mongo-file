Mongofile Plugin
================

The Mongofile plugin provides a FileService that saves, retrieves and deletes files from a MongoDB file store.

Each file is stored in a MongoDB collection (bucket), named after the domain class name.  

The following methods are available on FileService:

saveFile(Class domainClass, Long id)
deleteFile(Class domainClass, Long id)
getFile(Class domainClass, Long id)
deliverFile(HttpServletResponse response, Class domainClass, Long id, boolean asAttachment)

The last method writes the file to the response, either directly or as an attachment.

