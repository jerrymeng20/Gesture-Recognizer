# Gesture Recognizer Ver. 1.0.1
(c) 2021 Yanchen Meng (y58meng). <br>
Last revised: April 9, 2021.
## Introduction
Gesture Recognizer is an Android app that allows users to create, apply, and manage one-stroke touch gestures.
A one-stroke gesture allows users to perform a gesture with one single touch on the screen: from putting a finger
down to start the gesture, then sliding the finger to draw a gesture stroke, to releasing the finger to complete the gesture.
## Gesture examples
The examples below are some typical one-stroke touch gestures, where the dot indicates the starting point.
![gesture example](./app/src/main/res/resources/gestures.png?raw=true)
## Application Functionalities
* **Add gestures**: Users are able to add new gestures to the gesture library by directly drawing a one-stroke gesture on the screen,
with a real-time visualization of the path. The application allows the user to commit the stroke they have drawn and clear/cancel it if they make a mistake.
The user is able to try as many times as possible before committing the gesture. If the user continues to draw multiple strokes, only the last stroke is kept
(i.e., supporting only one-stroke gesture). When a gesture is being added, the user is able to input a name (in text) for that gesture (e.g., on a pop-up dialog).
* **Manage gestures**: Users are able to manage their gesture library consisting of the gestures added. The application allows the user to browse all the gestures
in the library (e.g., in a list view) with thumbnails of the paths. It also allows for modifying (i.e., replacing it with a new one) and removing a gesture from the library.
The gesture library is persisited after exiting and relaunching the app.
* **Recognize gestures**: Users are able to input a gesture on the screen and obtain the top 3 recognized gestures sorted from the best to the least matches (if more than 3
gestures are in the library; otherwise, display all the gestures in the order). The matched gestures are shown in an intuitive manner with thumbnails of the paths and their
user-defined names. The computed point-wise path distances could also be shown for debuging/information. The user is able to input as many gestures as they want to obtain
different recognition results. Again, this gesture input is constrained to one-stroke gestures.
## App ScreenShots
![gesture example](./app/src/main/res/resources/screenshot1.png?raw=true)
![gesture example](./app/src/main/res/resources/screenshot2.png?raw=true)
![gesture example](./app/src/main/res/resources/screenshot3.png?raw=true)
## Development Information
* Developer: Yanchen Meng (y58meng)
* Student Number: 20746115
* Operating System: Edition	Windows 10 Home build 19042.867
* Java Version: 11.0.8
* Android Version: 11.0
## References
* canvas finger drawing source code reference: https://stackoverflow.com/questions/16650419/draw-in-canvas-by-finger-android
* card view sourcecode reference: https://medium.com/android-beginners/cardview-android-example-beginners-dde933585261
* delete.png: https://toppng.com/uploads/preview/recycling-bin-vector-delete-icon-png-black-11563002079w1isxqyyiv.png
* modify.png: http://cdn.onlinewebfonts.com/svg/img_388620.png
## Copyright
(c) 2021 Yanchen Meng, All rights reserved.
