class MongofileGrailsPlugin {
    // the plugin version
    def version = "0.92"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [mongodb:"1.0.0.RC3"]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Mongofile Plugin" // Headline display name of the plugin
    def author = "Craig Raw"
    def authorEmail = "craig@quirk.biz"
    def description = '''\
The Mongofile plugin provides a FileService that saves, retrieves and deletes files from a MongoDB file store.

Each file is stored in a MongoDB collection (bucket), named after the domain class name.  
'''

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Quirk Labs", url: "http://www.quirklabs.co.za" ]

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }
}
