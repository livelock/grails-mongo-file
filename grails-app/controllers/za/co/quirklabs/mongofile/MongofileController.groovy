package za.co.quirklabs.mongofile

import com.mongodb.gridfs.GridFSDBFile

class MongofileController {
    def mongofileService

    def deliver(FileCommand fileCommand) {
        String bucket = mongofileService.getBucketFromString(fileCommand.domainClass, fileCommand.fieldName)
        GridFSDBFile file = mongofileService.getFile(bucket, fileCommand.id)
        mongofileService.deliverGridFSFile(response,file,null,fileCommand.attachment)
    }
}

class FileCommand {
    String domainClass
    Long id
    String fieldName
    boolean attachment
}
