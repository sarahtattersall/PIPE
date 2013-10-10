import os
import json

# Loads JSON internal_libs.json file
def get_internal_libs():
	json_data=open('internal_libs.json')
	data = json.load(json_data)
	json_data.close()
	
	for (key, value) in data.iteritems():
		print key

	return data

def getMavenInstallCall(libFile, artifactId, versionId):
	commandLineCall = "mvn install:install-file"
	groupIdArg = "-DgroupId=uk.ac.imperial"
	packgingArg = "-Dpackaging=jar"

	fileArg = "-Dfile=" + libFile
	artifactIdArg = "-DartifactId=" + artifactId
	versionArg = "-Dversion=" + versionId

	commandLine = (" ").join([commandLineCall,
		fileArg,
		groupIdArg,
		artifactIdArg,
		versionArg,
		packgingArg])

	return commandLine


# Expects data in format
# { <jar_name> : { artifactId : <artifact>, version : <version>}, ...}
def process_libs(libs, path):

	for (lib, value) in libs.iteritems():
		libFile = path + "/" + lib + ".jar"
		commandLine = getMavenInstallCall(libFile, value.get('artifactId'), value.get('version'))
		os.system(commandLine)

def print_pom_dependencies(libs):
	dependencyOpenTag = '<dependency>'
	dependencyCloseTag='</dependency>'
	groupIDTag = '<groupId>uk.ac.imperial</groupId>'
	for (lib, value) in libs.iteritems():
		artifactIdTag = '<artifactId>' + value.get('artifactId') + '</artifactId>'
		versionTag = '<version>' + value.get('version') + '</version>'  

		dependency = ('\n').join([dependencyOpenTag,
								  "    " + groupIDTag,
								  "    " + artifactIdTag,
								  "    " + versionTag,
								  dependencyCloseTag])
		print dependency
		print 

if __name__ == '__main__':
	import sys
	if (len(sys.argv) < 2):
		print "Please enter library locations"
		sys.exit(1)

	libData = get_internal_libs();
	process_libs(libData, sys.argv[1])

	print "DEPENDENCIES TO INCLUDE IN POM.XML:"
	print_pom_dependencies(libData)