# This is an **OpenHAB** android client #

This repo is a [HABDroid](https://github.com/openhab/openhab/wiki/HABDroid) clone from december 2013 with some extra functionality.
Extended functionality:

**Room Flipper** - A 3D navigation of your sitemap.
 
(In main activity option menu -> *Room Flipper*)

* Each site group can be attached to a custom picture of a Room
* Each room can have OpenHAB widgets attached to it. Room design in run-time.
* Custom connections between rooms during run-time.
* Navigate sideways in 8 directions by flipping the rooms. Navigate up/down by pinching in and out.


**Rules** - Add rules in a *Tasker*-like way.

NOTE! this has nothing to do with the design-time rules on the server side.

(In main activity option menu -> *Room Flipper* -> action bar -> ![rule icon.png](https://bitbucket.org/repo/dR6KpB/images/3743440785-rule%20icon.png))


* Rules can be added in a *Tasker*-like way.
* Each Rule contains an tree of Operations that will run a bunch of Actions when the result of the operation tree becomes TRUE.
* There are two types of Actions. *Command* will change the state of an OpenHAB item. *Message* will send a notification to both Android and Wear devices.

**Android Wear** - Support for Android Wear devices

(In main activity option menu -> *Room Flipper* -> option menu -> ![openhab wear.png](https://bitbucket.org/repo/dR6KpB/images/1391156405-openhab%20wear.png))

* This menu option will fire a notification that will be shown on an Android Wear device.
* A Wear notification can also be sent with a rule action. 
* The Wear device can open the notification and send voice commands to OpenHAB system.
* These voice commands is a lot more powerful and accurate (forgiving) than the original one.

## "Wanna try out now!" ##
1. Download and install the latest APK that are published in the [Releases](https://github.com/tonyalpskog/OpenHAB_Room_Flipper/releases) area. 
2. Run the app and follow the walkthrough named *Demo walkthrough.txt* that is found in the source root.

## "How does this stuff work?" ##
Please read about the [OpenHAB](http://www.openhab.org/) server and the REST API.

## "I would like to contribute." ##
Hey, you're my man!

There is a ton of work to do so you are most welcome.

Please note me and tell me what you would like to contribute with.

**So what is the status of the project?**


To save me some time I had to cut the corners a lot while developing so please bear with me.

* The source repo is cut off from the original repo.
* Some boilerplate code is missing.
* Device rotation is not supported in most of the views.
* Nothing is stored persistent.
* Missing application threading that would make it more responsive.
* The server sitemap integration is limping.
* No custom view components that would make the app look much better.
* Some hard coded strings with no translation.
* No UI unit tests is written and some logic unit tests is missing.

Some refactoring has been made in the original code in order to be able to use it for extended functionality.

Pretty much everything extended is experimental and totally unfinished.
