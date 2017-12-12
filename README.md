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

Name | Optional | Description | Example | Default
---- | -------- | ----------- | ------- | -------
-de | **mandatory** | Specifies the location of the input file | -de "~/design.des" | N/A
-o | **mandatory** | Specifies the location for the output file | -o "~/design_routed.ses" | N/A
-od | **optional** | Specifies the location for the .des output file | -o "~/design_routed.des" | N/A
-t | **optional** | Sets a timeout value for the autorouter, in seconds | -t 45 | 60
-l | **semi-optional** | Lists all the layers which should be actively routed. Mandatory if other parameters referencing layers are specified. Should list the parameters as comma-seperated layer names. | -l F.Cu,B.Cu | All layers
-ld | **optional** | A list of preferred directions for traces by layer. Same order as in -l, which is required. h for horizontal, v for vertical. | -ld h,v | Automatic
