# MapTracker Android - An Android client for MapTracker

With [MapTracker](https://github.com/Ekito/MapTracker), we've explained how to handle real time events and display it in a map.

This project is a client for Map Tracker. Here we show how to get the best location provider following a specific strategy (best accuracy or lowest power consumption) and send coordinates to the server on location updates.

## Required libraries

After cloning the repository, you will need to include the following libraries in `libs/`:

- [Android-async-http](http://loopj.com/android-async-http/) to handle RESTful requests.
- [Android Support Package](http://developer.android.com/sdk/compatibility-library.html) to work on every versions of Android SDK

## To run the project in local machine

First, launch the server by following [this post](http://www.ekito.fr/portail/playing-2-0-with-twitter-bootstrap-websockets-akka-and-openlayers?lang=en).

When the server's launched and the Android project's ready, include your computer IP address in `MTRestClient.java`, the variable `BASE_URL`.

Run the Android app as usual (on a device or emulator) and you should see your position appearing in your web browser.

## Playing with variables...

This example shows how to use different location providers following a specific strategy. In `MTActivity.java`, change `DEFAULT_STRATEGY` to:
- `CostStrategy.LOW` for low accuracy, low power consumption
- `CostStrategy.HIGH` for high accuracy, high power consumption

`LOC_MIN_TIME` is the minimum time interval for location updates, in milliseconds. `LOC_MIN_DIST` is the minimum distance interval for location updates, in meters. Play with these two values for your use case.

Please [let us know](https://github.com/Ekito/MapTracker-Android/issues) if you find any issues.
