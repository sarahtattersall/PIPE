# PIPE 5 [![Build Status](https://travis-ci.org/sarahtattersall/PIPE.png?branch=master)](https://travis-ci.org/sarahtattersall/PIPE)

A tool for creating and analysing Petri nets, migrated from [Sourceforge](http://pipe2.sourceforge.net/about.html). A user-guide can be found [here](http://sarahtattersall.github.io/PIPE/) for how to use PIPE 5's features.

## About ##
PIPE is an open source, platform independent tool for creating and analysing Petri nets including 
Generalised Stochastic Petri nets. Petri nets are a popular way for modelling concurrency and synchronisation 
in distributed systems. To learn more about Petri nets, you can start by reading the 2006/7 MSc. 
project report available [here](http://pipe2.sourceforge.net/docs.html).

## Installation ##
### Uber jar ###
If you just wish to use the latest release download the latest uber-jar from [the release page](https://github.com/sarahtattersall/PIPE/releases), double click and go!


### Building the project manually ###
First check out the release you're interested in e.g:
 
    $ git checkout PIPE-5.0.0-beta-3
    
Then to build the ```jar``` execute the following from within the PIPE root directory:

    $ mvn install
    
Once finished you should see the creates ```jars``` in the directory ```pipe-gui/target```.


### Execution ###
In order to run the PIPE GUI execute the following commands from within the PIPE root directory:
    
    $ mvn install
    $ mvn exec:exec -pl pipe-gui
    
### Note: local libs ###
There are some internal libraries, which need to be found for maven. For the mean time they are located in the project under ``src/local-libs`` and this directory is treated as a local library. When installing expect the following warning:

	[WARNING] The POM for internal:XXX is missing, no dependency information available
	
The original method for these local libraries required running a Python script to install the local libraries using ``mvn install``, however this new method removes the need for a pre-install step).

If you know of a better way to do this, please raise it in the issues section.


## Contributing ##

Just follow the following recommended process:

- Fork it
- Create your feature branch (`git checkout -b my-new-feature`)
- Ensure your new code is tested thoroughly
- Commit your changes (`git commit -am 'Add some feature'`)
- Push to the branch (`git push origin my-new-feature`)
- Create new Pull Request
