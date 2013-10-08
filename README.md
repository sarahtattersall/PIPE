# PIPE 2 #

A tool for creating and analysing Petri nets, migrated from http://pipe2.sourceforge.net/about.html

## About ##
PIPE2 is an open source, platform independent tool for creating and analysing Petri nets including 
Generalised Stochastic Petri nets. Petri nets are a popular way for modelling concurrency and synchronisation 
in distributed systems. To learn more about Petri nets, you can start by reading the 2006/7 MSc. 
project report available [here](http://pipe2.sourceforge.net/docs.html).

PIPE2 began life in 2002/3 as an MSc. Group Project at the Department of Computing, Imperial College London called 
"The Platform Independent Petri net Editor PIPE". This is now the official branch of that project. 
It is still being maintained as an on-going project at the college.


### Imperial College Projects ###
* 2002/3 James Bloom, Clare Clark, Camilla Clifford, Alex Duncan, Haroun Khan and Manos Papantoniou create PIPE
* 2003/4 Tom Barnwell, Michael Camacho, Matthew Cook, Maxim Gready, Peter Kyme and Michael Tsouchlaris 
continue the project as PIPE2 with substantial bug fixes and user interface enhancements.
* 2005 Nadeem Akharware adds advanced GSPN analysis capabilities.
* 2006/7 Edwin Chung, Tim Kimber, Benjamin Kirby, Will Master and Matt Worthington made bug fixes, 
code efficiency improvements and added zoom to the GUI and reachability graph generation capability.


## Installation ##
### Once only ###
PIPEv4.3.0 makes use of Maven as a dependency manager and build system. Currently it makes use of a few
internal libraries located in ```src/libs```. To install these for use with Maven there is a Python script 
```mvn_libs/add_maven_dependencies.py``` to install these to your Maven local repository. It takes one command line
argument which is the path to the ```jar``` files.
It makes use of the libs declared in ```mvn_libs/internal_libs.json``` which is a JSON object of the format:

    {
        ...
    	<jar_basename> : {
    		"artifactId" : <artifact_name>,
    		"version" : <version_number>
    	},
    	...
    }

For example

    {
    	"Jama-1.0.2" : {
    		"artifactId" : "jama",
    		"version" : "1.0.2"
    	},
    	...
    }
    
If you add any other internal ```jar``` files. Make sure they follow the same format as this.
To run this script execute the following:

    $ cd mvn_libs
    $ python add_maven_dependencies.py  ../src/lib/
    

    
### Build jar ###
To build the ```jar``` library execute the following from within the PIPE root directory:

    $ mvn install
    
Once finished you should see the file ```target/PIPE-4.3.0.jar```.


### Execution ###
In order to run PIPE tool execute the following from within the PIPE root directory:

    $ mvn exec:java

