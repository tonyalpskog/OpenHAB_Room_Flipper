This is an **OpenHAB** android client. Please read a lot about the [OpenHAB](http://www.openhab.org/) server and it´s settings before trying to understand how this app works.


1. Setup a local server
2. Read about OpenHAB items and sitemaps

This repo is a [HABDroid](https://github.com/openhab/openhab/wiki/HABDroid) clone from december 2014 with some extra functionality.
The client needs connection to a OpenHAB server to be able to show any kind of data.
The main activity in the app has *Settings* in the option menu. By default the app is in demo mode = connection to a remote public demo server.

APK:s are published in the [Downloads](https://bitbucket.org/tonyalpskog/open-hab-android-client/downloads) area. 

Major refactoring has been done to the original code in order to be able to use it for extended functionality.
Pretty much everything extended is experimental and totally unfinished. Nothing is saved persistent.
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
* The Wear device can open the notification and send voice commands to OpenHAB system.
* These voice commands is a lot more powerful and accurate (forgiving) than the original one.