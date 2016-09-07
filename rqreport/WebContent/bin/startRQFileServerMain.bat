@echo off
set rqlibpath=..\WEB-INF\lib
set rqclasspath=%classpath%
set rqclasspath=%rqclasspath%;%rqlibpath%\activation.jar;
set rqclasspath=%rqclasspath%;%rqlibpath%\cos.jar;
set rqclasspath=%rqclasspath%;%rqlibpath%\jazzlib.jar;
set rqclasspath=%rqclasspath%;%rqlibpath%\jce-jdk13-125.jar;
set rqclasspath=%rqclasspath%;%rqlibpath%\jdom.jar;
set rqclasspath=%rqclasspath%;%rqlibpath%\log4j-1.2.13.jar;
set rqclasspath=%rqclasspath%;%rqlibpath%\mail.jar;
set rqclasspath=%rqclasspath%;%rqlibpath%\ojdbc14.jar;
set rqclasspath=%rqclasspath%;%rqlibpath%\sapjco.jar;
set rqclasspath=%rqclasspath%;..\WEB-INF\classes;

java -classpath %rqclasspath% com.sds.rqreport.service.fileservice.RQFileServerMain 10523 D:/ImageRoot