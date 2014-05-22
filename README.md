# PIPE  [![Build Status](https://travis-ci.org/sarahtattersall/PIPE.png?branch=master)](https://travis-ci.org/sarahtattersall/PIPE)


A tool for creating and analysing Petri nets, migrated from [Sourceforge](http://pipe2.sourceforge.net/about.html). A user-guide can be found [here](http://sarahtattersall.github.io/PIPE/) for how to use PIPE's features.

## About ##
PIPE is an open source, platform independent tool for creating and analysing Petri nets including 
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
### Build jar ###
To build the ```jar``` library execute the following from within the PIPE root directory:

    $ mvn install
    
Once finished you should see the file ```target/PIPE-4.3.0.jar```.


### Execution ###
In order to run the PIPE GUI execute the following commands from within the PIPE root directory:
   
    $ mvn install
    $ mvn exec:exec -pl pipe-gui
    
### Note: local libs ###
There are some internal libraries, which need to be found for maven. For the mean time they are located in the project under ``src/local-libs`` and this directory is treated as a local library. When installing expect the following warning:

	[WARNING] The POM for internal:XXX is missing, no dependency information available
	
The original method for these local libraries required running a Python script to install the local libraries using ``mvn install``, however this new method removes the need for a pre-install stp).

If you know of a better way to do this, please raise it in the issues section.


## Contributing ##

Just follow the following recommended process:

- Fork it
- Create your feature branch (`git checkout -b my-new-feature`)
- Ensure your new code is tested thoroughly
- Commit your changes (`git commit -am 'Add some feature'`)
- Push to the branch (`git push origin my-new-feature`)
- Create new Pull Request
