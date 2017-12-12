FreeRouting
===========

Java Based Printed Circuit Board Routing Software from FreeRouting.net written by Alfons Wirtz.

http://www.freerouting.net/fen/viewtopic.php?f=4&t=255

by alfons » Sat Mar 08, 2014 12:07 pm

Because I am no more maintaining the Freerouting project since 4 years and future Java versions may block my Freerouting Java Web Start application completely, I finally decided to open the source of the Freerouting project under the GNU public license version 3.

I have attached the complete source code of the Freerouting project. Please feel free downloading and using the sources according to the GPLv3 license.

FreeRoutingCLI
==============
This modification of the FreeRouting software enables it to be run via a simple CLI interface without any user interface.

Parameters
----------
The following parameters can be supplied to the program:

Name | Optional | Description | Default
---- | -------- | ----------- | -------
-de | **mandatory** | Specifies the location of the input file | N/A
-o | **mandatory** | Specifies the location for the output file | N/A
-t | **optional** | Sets a timeout value for the autorouter, in seconds | 60
