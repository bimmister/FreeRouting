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

Name | Optional | Description | Allowed values | Example | Default
---- | -------- | ----------- | -------------- | ------- | -------
-de | **mandatory** | Specifies the location of the input file | Any file with a .dsn ending | -de "~/design.dsn" | N/A
-o | **mandatory** | Specifies the location for the output file | Any file path with a .ses ending | -o "~/design_routed.ses" | N/A
-od | **optional** | Specifies the location for the .dsn output file | Any file path with a .dsn ending | -o "~/design_routed.dsn" | N/A
-t | **optional** | Sets a timeout value for the autorouter, in seconds. 0 for no timeout. | Any >= 0 integer number | -t 45 | 60
-l | **semi-optional** | Lists all the layers which should be actively routed. Mandatory if other parameters referencing layers are specified. Should list the parameters as comma-seperated layer names. | A list of strings with , as a separator | -l F.Cu,B.Cu | All layers
-ld | **optional** | A list of preferred directions for traces by layer. Same order as in -l, which is required. h for horizontal, v for vertical. | a list of h and v with , as a separator | -ld h,v | Automatic
-v | **optional** | Specifies whether the autorouter is allowed to create vias. | y or n | -v y | y
-fo | **optional** | Specifies whether the autorouter should start with a fanout step. | y or n | -fo y | n
-ar | **optional** | Specifies whether the autorouter should include an autorouting step. | y or n | -ar y | y
-pr | **optional** | Specifies whether the autorouter should include a post-routing optimization step. | y or n | -pr y | y
-vc | **optional** | The cost value of creating a via hole while autorouting | positive integer values | -vc 35 | 50
-ppvc | **optional** | The cost value of creating a powerplane via hole while autorouting | positive integer values | -ppvc 20 | 5
-sp | **optional** | The start pass value | integer values 1-99 | -sp 1 | Automatically selected
-rsc | **optional** | The startcost of a ripup for the autorouter | -rsc | 100
-pdc | **optional** | The preferred direction trace cost, by layer | float values separated by , and with . as a decimal point | -pdc 1.2,1.3
-apdc | **optional** | The trace cost against the preferred direction, by layer | float value separated by , and with . as a decimal point | 5.5,5.7

Error codes
-----------
The following error codes can be returned, along with a (hopefully) helpful message in case of errors:

Code | Description
---- | -----------
1 | Unknown error
2 | Invalid input parameter
3 | I/O error with reading or writing files
4 | Design problems
5 | Timeout
6 | Routing interrupted
