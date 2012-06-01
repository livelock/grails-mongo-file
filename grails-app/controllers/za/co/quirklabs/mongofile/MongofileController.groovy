package za.co.quirklabs.mongofile

import com.mongodb.gridfs.GridFSDBFile

class MongoFileController {
    def mongoFileService

    def exists(FileCommand fileCommand) {
        GridFSDBFile file = getGridFSFile(fileCommand)
        file != null
    }

    def deliver(FileCommand fileCommand) {
        GridFSDBFile file = getGridFSFile(fileCommand)
        mongoFileService.deliverGridFSFile(response,file,null,fileCommand.attachment)
    }
    
    GridFSDBFile getGridFSFile(FileCommand fileCommand) {
        String bucket = mongoFileService.getBucketFromString(fileCommand.domainClass, fileCommand.fieldName)
        mongoFileService.getFile(bucket, fileCommand.id)
    }
}

class FileCommand {
    String domainClass
    Long id
    String fieldName
    boolean attachment
}
