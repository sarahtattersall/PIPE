# PIPE 5 [![Build Status](https://travis-ci.org/sarahtattersall/PIPE.png?branch=master)](https://travis-ci.org/sarahtattersall/PIPE)

A tool for creating and analysing Petri nets, migrated from [Sourceforge](http://pipe2.sourceforge.net/about.html). A user-guide can be found [here](http://sarahtattersall.github.io/PIPE/) for how to use PIPE 5's features.

PIPE 5 is currently in beta stage due to an entire re-write of the back end and so is missing most of the analysis modules. If you require Petri net analysis, please use PIPE 4.

## About PIPE ##
PIPE is an open source, platform independent tool for creating, simulating and analysing Petri nets including 
Generalised Stochastic Petri nets. Petri nets are a popular way for modelling concurrency and synchronisation 
in distributed systems and to learn more about Petri nets, you can start by reading the 2006/7 MSc. 
project report available [here](http://pipe2.sourceforge.net/docs.html).

## How to cite PIPE ###
If you use PIPE in your research, we would be greatful if you would cite the relevant publications:
* N.J. Dingle, W.J. Knottenbelt and T. Suto. [PIPE2: A Tool for the Performance Evaluation of Generalised Stochastic Petri Nets](http://www.doc.ic.ac.uk/~wjk/publications/dingle-knottenbelt-suto-per-2009.pdf) (PDF format). *ACM SIGMETRICS Performance Evaluation Review* (Special Issue on Tools for Computer Performance Modelling and Reliability Analysis), Vol. 36(4), March 2009, pp. 34-39.
* P. Bonet, [C.M. Llado](http://dmi.uib.es/~cllado/), R. Puijaner and [W.J. Knottenbelt](http://www.doc.ic.ac.uk/~wjk/). [PIPE v2.5: A Petri Net Tool for Performance Modelling](http://www.doc.ic.ac.uk/~wjk/publications/bonet-llado-knottenbelt-puijaner-clei-2007.pdf) (PDF format). *Proc. 23rd Latin American Conference on Informatics (CLEI 2007)*, San Jose, Costa Rica, October 2007.

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
