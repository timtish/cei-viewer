cei-viewer
==========

IBM MQ queues viewer.

You can:

[+] check backout queue depth (by out text or error code)


example check-backouts.bat
```cmd
@echo off

set libs="C:\Program Files\IBM\WebSphere MQ\java\lib"
set classpath=viewer.jar;commons-cli-1.2.jar;%libs%/com.ibm.mq.jar;%libs%/com.ibm.mq.headers.jar;%libs%/com.ibm.mq.jmqi.jar;%libs%/com.ibm.mq.pcf.jar;%libs%/jta.jar;%libs%/connector.jar;%libs%/com.ibm.mq.commonservices.jar 

set queues=SERVICE.A.BACKOUT,REGISTER.BACKOUT

java -classpath %classpath% ru.mq.viewer.CeiViewer -s localhost -p 1414 -m SRV.QM -c SYSTEM.ADMIN.SVRCONN -q %queues%
```

sample check-backouts.sh 
```bash
#!/bin/bash
# show backout queues length

MQM_LIB=/opt/mqm/java/lib
CLASSPATH=viewer.jar;commons-cli-1.2.jar;$MQM_LIB/com.ibm.mq.jar;$MQM_LIB/com.ibm.mq.headers.jar;$MQM_LIB/com.ibm.mq.jmqi.jar;$MQM_LIB/com.ibm.mq.pcf.jar;$MQM_LIB/jta.jar;$MQM_LIB/connector.jar;$MQM_LIB/com.ibm.mq.commonservices.jar 

QUEUES=SERVICE.A.BACKOUT,REGISTER.BACKOUT

java -classpath $CLASSPATH ru.mq.viewer.CeiViewer -s localhost -p 1414 -m SRV.QM -c SYSTEM.ADMIN.SVRCONN -q $QUEUES
```


[+] show all messages from specified queue 


```bash
java -classpath viewer.jar;commons-cli-1.2.jar;/opt/mqm/java/lib/* ru.mq.viewer.CeiViewer -m SRV.QM -c SYSTEM.ADMIN.SVRCONN -v -e ORANGE.QUEUE
``` 


[+] show Common Event Infrastructurt messages


```bash
java -classpath viewer.jar;commons-cli-1.2.jar;/opt/mqm/java/lib/* ru.mq.viewer.CeiViewer -s localhost -p 1414 -m SRV.QM -c SYSTEM.ADMIN.SVRCONN -v -e CEI.EVENTS
``` 
