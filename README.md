Leapo
=====

Experiment building an XOrg controller for the leap motion implemented in Java.

Needs the leap daemon running (/usr/sbin/leapd), and TCP connections enabled on local XOrg server. (XOrg not started with -nolisten)

To build needs the escher jar from http://sourceforge.net/projects/escher/ installed with 

  $ mvn install:install-file -DlocalRepositoryPath=./repo -DcreateChecksum=true -Dpackaging=jar -Dfile=escher-0.3.jar -DgroupId=net.sf -DartifactId=escher -Dversion=0.3







