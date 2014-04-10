---
layout: default
title: What's changed?
---

The main focus of PIPE 5 so far has been to improve the codebase in order to reduce bugs and make it more maintainable for future years. Whilst the user interface looks largely the same, a lot of work has gone in to improve the backend code so that the quality of the application can be improved. A quick overview of what's changed so far:

* **Migration of the project from Sourceforge to GitHub** - Amongst other features GitHub is great for social coding.
* **Separation of the logic from the views** - An entirely new backend model of a Petri net has been written. This backend can also be used with other applications or in a stand-alone project and provides all the functionality of modeling a Petri net without the GUI. The backend also provides tools for loading, saving and cloning a Petri net. It also provides a DSL for creating Petri nets in a very readable manner.
* **Improved functional rates** - The functional rates for arcs and transitions now support their own grammar which is fully parsed using ANTLR. This change makes it easier to add new functionality in the future.
* **Bug fixing** - Numerous bugs have been fixed.

The only down side of these changes is that currently the modules have been disabled as they do not integrate with the new backend. We are highly aware that the biggest reason to use PIPE is for the module support, so we suggest in the mean time to continue with PIPE 4 if this is what you really need. On a very positive note the modules should be re-appearing very soon one by one!