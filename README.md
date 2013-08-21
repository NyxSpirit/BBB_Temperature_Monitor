BeagleBone Black Temperature Monitor
=============

Final Project for Java 143

A working server/client program that uses a BeagleBone Black and TMP102 Temperature Sensor to read real-time temperature
data and send that data over the internet.

The Server application runs on the BeagleBone Black and uses multiple threads to read the data, write to a log file, and send the data to the remote client.

The Client application receives the data sent from the Server application, allowing the user to view real-time temperature data remotely.


=============

*The ability to graph a time chart displaying temperature versus time has not yet been implemented. Client/Server interaction works over the internet with port-forwarding.
