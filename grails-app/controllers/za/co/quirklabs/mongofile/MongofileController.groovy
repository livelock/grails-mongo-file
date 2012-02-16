package za.co.quirklabs.mongofile

import com.mongodb.gridfs.GridFSDBFile

class MongofileController {
    def mongofileService

    def exists(FileCommand fileCommand) {
        GridFSDBFile file = getGridFSFile(fileCommand)
        file != null
    }

    def deliver(FileCommand fileCommand) {
        GridFSDBFile file = getGridFSFile(fileCommand)
        mongofileService.deliverGridFSFile(response,file,null,fileCommand.attachment)
    }
    
    GridFSDBFile getGridFSFile(FileCommand fileCommand) {
        String bucket = mongofileService.getBucketFromString(fileCommand.domainClass, fileCommand.fieldName)
        mongofileService.getFile(bucket, fileCommand.id)
    }
}

class FileCommand {
    String domainClass
    Long id
    String fieldName
    boolean attachment
}
