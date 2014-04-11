---
layout: post
title: Editing a Petri net
post-id: edit
---

A Petri net can be built up by using the appropriate icons along the task bar. 


#### Adding a token ![New Token icon](images/taskbar/SpecifyTokenClasses.png) 

In order to add a token to the Petri net click on the new token icon or press Ctrl-Shift-T. This will pop up the token editor window in which tokens can be added by giving them a name and a color.



![Add Token Window](images/taskbar/add_token.png)

The active token can be changed via the token drop down menu.

![Change active token](images/taskbar/change_token.png)

#### Adding and editing a place ![Place icon](images/taskbar/place.png)

The place component tool be selected by clicking the icon with a round circle.

Once selected click anywhere on the canvas to create a new place in this location. The place will be automatically named for you.


In order to edit place attributes, such as it's name and capacity, right click the place and click 'Edit Place'. The place editor window will then pop up where you can enter the new details for the Place.

![Edit Place](images/taskbar/edit_place.png)


The active token can be added to or removed from places by selecting the relevant icons next to the active token menu ![Place icon](images/taskbar/AddToken.png) ![Place icon](images/taskbar/DeleteToken.png). In order to change which token is added, select the token you wish to place from the menu, then click on the place to add tokens. Alternatively adding tokens can be done in the place editor window as seen above.


#### Adding and editing a Transition ![Immediate transition icon](images/taskbar/immediate_transition.png) ![Timed transition icon](images/taskbar/timed_transition.png) 
Similarly a new transition can be added by clicking either the black rectangle which represents an immediate transition, or the unfilled rectangle which represents a timed transition. The transition can then be created anywhere by clicking on the canvas. It is automatically named, just like when adding a place.
 
Right clicking on a transition and clicking 'Edit Transition' allows you to edit the transitions attributes and swap between a timed or immediate transition.

![Edit Transitionn](images/taskbar/edit_transition.png)

A timed transitions rates may either be static or <a href="#functional">functional</a>. 

#### Adding and editing an Arc ![Normal Arc icon](images/taskbar/arc.png) ![Inhibitor Arc icon](images/taskbar/inhibitor_arc.png) 
Click on either the normal arc (pointed arrow head), or the inhibitor arc (round arrow head) icon and then select the components you wish to join.

A normal arc can join either a place to a transition or a transition to a place. An inhibitor arc can only join a place to a transition and will only fire if the place contains no tokens.

When adding an arc clicking on free space within the canvas will add points along the arcs path, shift clicking will create curved points. Pressing `Esc` whilst creating an arc will cancel it's creation.

![Created arcs](images/taskbar/arcs.png) 


Again the number of tokens an arc requires can either be static or functional. You can edit the arcs weight by right clicking on the arc and selecting 'Edit Weight'. This brings up the weight editor where a weight can be added for each token declared.

![Edit Arc Weight](images/taskbar/arc_weight_editor.png)

#### Adding a Rate Parameter  ![Rate Parameter icon](images/taskbar/rate_param.png) 
Rate parameters are useful because they allow you to create a shared rate for timed transitions. Modifying this rate will effect any transitions that reference this rate. Click on the rate parameter icon along the task bar and enter a name and value for this rate.

![Rate Parameter editor](images/taskbar/rate_param_editor.png) 

This rate can now be selected in the transitions editor window. 

![Specify transition rate parameter](images/taskbar/transition_rate_parameter.png) 

In a similar manner rate parameters can be deleted from the Petri net. Any transitions that make use of this rate parameter will have their rates set to the parameters value at the time of deletion.




