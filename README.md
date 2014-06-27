This is a OpenHAB android client. Please read a lot about the [OpenHAB](http://www.openhab.org/) server and settings before trying to understand how this app works.
1. Setup a local server
2. Read about OpenHAB items and sitemaps

This repo is a [HABDroid](https://github.com/openhab/openhab/wiki/HABDroid) clone with a lot of extra functionality.
The client needs connection to a OpenHAB server to be able to show any kind of data.
The main activity in the app has *Settings* in the option menu. By default the app is in demo mode = connection to a demo server in Germany(?).

Major refactoring has been done to the original code in order to be able to use it for extended functionality.
Pretty much everything extended is experimental and totally unfinished.
Extended functionality:

(In main activity option menu -> *Room Flipper* -> option menu -> *Edit*)
- Each site group can be attached to a graphical Room
- Each room can have OpenHAB widgets attached to it. Room design in run-time.
- All rooms is (during run-time design) connected to each other in a 3D-way. (8 directions + up/down = 10 directions)

(In main activity option menu -> *Room Flipper* -> option menu -> *Rules*)
NOTE! this has nothing to do with the design-time rules on the server side.
- Rules can be added in a *Tasker*-like way.
- Each Rule contains an Operation that will run a bunch of Actions when the criterias of the operation becomes TRUE.

(In main activity option menu -> *Room Flipper* -> option menu -> *Clock*)
- This menu option will fire a notification that will be shown on an Android Wear device.
- The Wear device can open the notification and send voice commands to OpenHAB system.
- These voice commands is a lot more powerful and accurate (forgiving) than the original one.
