---
layout: post
title: Editing a Petri net
post-id: edit
---

A Petri net can be built up by selecting the appropriate the icons along the task bar. 


#### Adding a Token ####
In order to add a token click on the new token icon. This will pop up the token editor window in which tokens can be added by giving them a name and a color.

The active token can be changed via the token drop down menu.

#### Adding and editing a Place ####
A place component can be added by clicking the icon with a round circle. 
![Place icon]({{ site.url }}/images/taskbar/place.png)
Once selected click anywhere on the canvas to create a new Place in this location. The Place will be automatically named for you.

In order to edit place attributes, such as it's name and capacity, right click the place and enter the new details in the place editor.

Furthermore tokens can be added to or removed from Places by clicking on the relevant icons. In order to change which token is added, select the token you wish to place from the token drop down menu.


#### Adding and editing a Transition ####
Similarly a new transition can be added by clicking either the black rectangle which represents an immediate transition, or the unfilled rectangle which represents a timed transition.

The transition can then be placed anywhere on the canvas and is automatically named.

Right clicking on a transition allows you to edit its name and other parameters associated with the transitions type.

A timed transitions rates may either be static or functional. 

#### Adding and editing an Arc ####
Click on either the normal arc, or the inhibitor arc icon and then select the components you wish to join.

A normal arc can join either a place to a transition or a transition to a place. An inhibitor arc can only join a place to a transition and will only fire if the place contains no tokens.

When adding an arc clicking on free space within the canvas will add points along the arcs path, shift clicking will create curved points. 

Again the number of tokens an arc requires can either be static or functional.

#### Adding a Rate Parameter ####
Rate parameters are useful because they allow you to create a shared rate for timed transitions. Modifying this rate will modify any transitions that reference this. Click on the rate parameter icon along the task bar and enter a name and value for this rate.

This rate can now be selected in the transitions editor window. 

In a similar manner Rate Parameters can be deleted from the Petri net.




