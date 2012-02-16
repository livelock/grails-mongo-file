package za.co.quirklabs.mongofile

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class MongofileTagLib {
    static namespace = "mongofile"
    
    LinkGenerator grailsLinkGenerator
    
    def createLinkTo = { attrs ->
		out << generateLink(attrs)
	}
	
	def img = { attrs ->
	    attrs.attachment = false
		def link = generateLink(attrs)
	    def excludes = ['domainClass', 'id', 'fieldName', 'attachment']
        def attrsAsString = ApplicationTagLib.attrsToString(attrs.findAll { !(it.key in excludes) })
        
        out << "<img src=\"${link.encodeAsHTML()}\"${attrsAsString} />"
	}
	
	def link = { attrs, body ->
	    if(!attrs.attachment) {
	        attrs.attachment = true
	    }
	    
		def link = generateLink(attrs)
	    def excludes = ['domainClass', 'id', 'fieldName', 'attachment']
        def attrsAsString = ApplicationTagLib.attrsToString(attrs.findAll { !(it.key in excludes) })
        
        out << "<a href=\"${link.encodeAsHTML()}\"${attrsAsString}>"
        out << body()
        out << "</a>"
	}

	def generateLink(Map attrs) {
	    def params = [id: attrs.id, domainClass: attrs.domainClass, fieldName: attrs.fieldName, attachment: attrs.attachment]
		grailsLinkGenerator.link(controller: 'mongofile', action: 'deliver', params: params)
	}
}
