class MongofileGrailsPlugin {
    def version = "0.98"
    def grailsVersion = "2.0 > *"
    def dependsOn = [mongodb:"1.0.0.RC3"]
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Mongofile Plugin"
    def author = "Craig Raw"
    def authorEmail = "craig@quirk.biz"
    def description = '''\
The Mongofile plugin provides a MongofileService that saves, retrieves and deletes files from a MongoDB file store. Furthermore, the domain classes have methods injected to handle these files.

Each file is stored in a MongoDB collection (bucket), named after the domain class name.  
'''
    def license = "APACHE"
    def organization = [ name: "Quirk Labs", url: "http://www.quirklabs.co.za" ]
    def developers = [[ name: "Juri Kuehn" ]]
    def scm = [ url: "https://github.com/quirklabs/grails-mongofile" ]

    def doWithDynamicMethods = { ctx ->
        for(domainClass in application.domainClasses) {
            domainClass.metaClass.mongofileExists = { String fieldName = '' -> 
                getMongofile(fieldName) != null
            }
            
            domainClass.metaClass.getMongofile = { String fieldName = '' -> 
                def mongofileService = org.codehaus.groovy.grails.commons.ApplicationHolder.application.getMainContext().getBean("mongofileService")
                if(mongofileService) {
                    return mongofileService.getFile(delegate.getClass(),id,fieldName)
                }
                
                null
            }
            
            domainClass.metaClass.saveMongofile = { org.springframework.web.multipart.commons.CommonsMultipartFile file, String fieldName = '' -> 
                def mongofileService = org.codehaus.groovy.grails.commons.ApplicationHolder.application.getMainContext().getBean("mongofileService")
                if(mongofileService) {
                    mongofileService.saveFile(file,delegate.getClass(),id,fieldName)
                }
            }
            
            domainClass.metaClass.deleteMongofile = { String fieldName = '' -> 
                def mongofileService = org.codehaus.groovy.grails.commons.ApplicationHolder.application.getMainContext().getBean("mongofileService")
                if(mongofileService) {
                    mongofileService.deleteFile(delegate.getClass(),id,fieldName)
                }
            }
        }
    }
}
