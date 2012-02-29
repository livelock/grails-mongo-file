package za.co.quirklabs.mongofile

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class MongofileTagLib {
    static namespace = "mongofile"
    
    LinkGenerator grailsLinkGenerator
    def mongofileService
    
    /**
       * Creates a link (not escaped) to the requested file.
       *
       * @attr domainClass REQUIRED the simple name of the domain class
       * @attr id REQUIRED the id of the domain class instance
       * @attr domainInstance use instead of domainClass and id
       * @attr fieldName the field name of the file, used when more than one file needs to be stored on the domain instance
       * @attr attachment if set to true the Content-Disposition header is set so the browser prompts to download the file
       */
    def createLinkTo = { attrs ->
		out << generateLink(attrs)
	}
	
	/**
       * Creates a image tag to the requested file/image.
       *
       * @attr domainClass REQUIRED the simple name of the domain class
       * @attr id REQUIRED the id of the domain class instance
       * @attr domainInstance use instead of domainClass and id       
       * @attr fieldName the field name of the file, used when more than one file needs to be stored on the domain instance
       */
	def img = { attrs ->
	    attrs.attachment = false
		def link = generateLink(attrs)
	    def excludes = ['domainClass', 'id', 'fieldName', 'attachment']
        def attrsAsString = ApplicationTagLib.attrsToString(attrs.findAll { !(it.key in excludes) })
        
        out << "<img src=\"${link.encodeAsHTML()}\"${attrsAsString} />"
	}
	
	/**
       * Creates a anchor tag to the requested file.
       *
       * @attr domainClass REQUIRED the simple name of the domain class
       * @attr id REQUIRED the id of the domain class instance
       * @attr domainInstance use instead of domainClass and id       
       * @attr fieldName the field name of the file, used when more than one file needs to be stored on the domain instance
       * @attr attachment if set to true the Content-Disposition header is set so the browser prompts to download the file
       */
	def link = { attrs, body ->
	    if(!attrs.attachment) {
	        attrs.attachment = true
	    }
	    
		def link = generateLink(attrs)
	    def excludes = ['domainClass', 'id', 'fieldName', 'attachment']
        def attrsAsString = ApplicationTagLib.attrsToString(attrs.findAll { !(it.key in excludes) })
        def text = body()
        text = text.length() ? text : getFileName(attrs)
        
        out << "<a href=\"${link.encodeAsHTML()}\"${attrsAsString}>"
        out << text
        out << "</a>"
	}

	def generateLink(Map attrs) {
	    def params = getDomainClassAndId(attrs)
	    
	    if(!params.domainClass) throw new IllegalArgumentException("[domainClass] attribute must be specified to for <mongofile:*> tags!")
	    if(!params.id) throw new IllegalArgumentException("[id] attribute must be specified to for <mongofile:*> tags!")
	    
	    if(attrs.fieldName) params.fieldName = attrs.fieldName
	    if(attrs.attachment) params.attachment = attrs.attachment
	    
		grailsLinkGenerator.link(controller: 'mongofile', action: 'deliver', params: params)
	}
	
	def getFileName(Map attrs) {
	    def params = getDomainClassAndId(attrs)
	    String bucket = mongofileService.getBucketFromString(params.domainClass, attrs.fieldName)
        def file = mongofileService.getFile(bucket, params.id)

	    file.getFilename()
	}
	
	def getDomainClassAndId(Map attrs) {
	    def domainClass = attrs.domainClass
	    def id = attrs.id
	    
	    if(!domainClass && !id && attrs.domainInstance) {
	        domainClass = attrs.domainInstance.class.simpleName
	        id = attrs.domainInstance.id
	    }
	    
	    [domainClass: domainClass, id: id]
	}
}
