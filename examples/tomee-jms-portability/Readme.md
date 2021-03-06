# Tomee-sample application to demo portability between ActiveMQ and IBM MQ
v1.0, created on 2-09-2017

### Description
This app demonstrates capability to achive portability/interoperability between ActiveMQ and IBM MQ for tomee based application of type MDB and Custom Java based JMS connection. It covers patterns and learnings as listed below

1. How to run multiple mdb containers in same JVM
2. Various ways of configuring IBM Queues/Topics vs AMQ Queues/Topics 
3. MDB configuration for IBM MQ vs AMQ, Annotations vs Configuration
4. Static vs Dynamic declaration/override of queues/topics
5. Use and importance of RA (Resource Adapaters), Connection Factory
6. Web service samples to write data to IBM Queues and AMQ Queues using injection of resources (connection factories and queue/topics)
7. JNDI reference to resources (see WMQReadBean configuration in ejb-jar.xml)


### Running this application
#### Environment
Application can be imported in IDE like eclipse as a maven project and is tested using apache-tomee-plume-7.0.2, apache-activemq-5.14.3 (running as standalone on localhost, IBM MQ 8.x.x (connections details hidden to avoid misuse, those how want to test can reach out to middleware team to get access to managed IBM MQ instance and AppWatch ))

#### Additional Libraries
In order to connect to IBM MQ, tomee requires additional libraries containing implementation for JCA Resource Adapter, IBM MQ connection factories, queue/topic resources, ActivationConfigs etc. These libraries come with IBM subscription and all of them are not available on maven central, below is list of all IBM specific jar files

com.ibm.mq.connector.jar  
com.ibm.mq.jar      
com.ibm.mq.pcf.jar   
com.ibm.mq.headers.jar    
com.ibm.mq.jmqi.jar   
com.ibm.mqjms.jar
providerutil.jar

com.ibm.mq.connector.jar can be extracted from wmq.jmsra.rar which is availble with IBM MQ installation (not available on internet)


#### tomee.xml: 
This file (located under tomee_home/conf is left blank as we would like our application to manage all resources.Any resource declared here would be available to all application deployed on the server.
(File not included in this git repo)
```
<?xml version="1.0" encoding="UTF-8"?>
<tomee>
</tomee>
```

#### catalina.properties: This file can be used to convinently provide -D java arguments as an input to tomee. However in real world these parameters would be configured using environment variables to avoid adding application specific properties in tomme conf (File not included in this git repo)

```
com.ibm.msg.client.commonservices.log.status=OFF

AMQReadBean2.activation.destination=overriden_queue_IMQReadBean
#queue destinations
amq.variable.destination=my_overriden_value
#Resource overrides
#amq_ra.ServerUrl=tcp://xxxxx.xxx.xxx.com:61616

#MDB Activation Config overrides
#Hirarchy for activation override rules (specific to generic)
#1  -D<deploymentId>.activation.<property>=<value>
#2. -D<ejbName>.activation.<property>=<value>
#3. -D<message-listener-interface>.activation.<property>=<value>
#4. -Dmdb.activation.<property>=<value>
#mdb.activation.destination=overriden_queue_value
WMQReadBean.activation.HostName=10.234.56.789
```

#### resources.xml: 
This file is application specific placeholder for resources (it will override any matching resources declared in tomee.xml) like resource-adapters, connection-factories, queues, topics, mdb-containers etc.
This example makes heavy use of this file which is under src/main/webapp/WEB-INF, for applications deployed as war file it get copied to ###/webapps/application-name/WEB-INF/resources.xml .

#### ejb-jar.xml: 
This file is located under src/main/resources/META-INF/  and contains application specific configuration for enterprise beans, in the demo we have configured our message beans in this file. Definitions in this file are equivalent of annotations , version="3.1" will support a combination of configured and annotated beans. One important difference between annotated beans and configured beans is as follows. If there are multiple containers defined or use in same application(Like this application uses amq-container and imq-container)
Any annotated bean is sequentially binded to the container resource, this can bring inpredictability and may result in a Bean getting binded to incorrect target container. As an example ChatBean MDB is intentionally commented as it may throw errors by getting incorrectly binded to IMQ. To deal with such problems, configuration based approach can be used in conjunction with openejb-jar.xml (as described below)

#### openejb-jar.xml: 
This file is located under src/main/resources/META-INF/  and contains additional mapping between ejb and targeted containers. It also has a deployment id which can be used to create multiple deployments for same ejb in a container or across containers. This is very useful when you want to bind a specific bean to desired container (example WMQReadBean should necessarily bind to simple-tomee-1.0/imq_container container)

#### web.xml: 
Typical web resource file, not used much in this tutorial app

#### beans.xml: 
To use @Inject, the first thing you need is a META-INF/beans.xml file in the module or jar. This effectively turns on CDI and allows the @Inject references to work. No META-INF/beans.xml no injection, period
Not used in this tutorial app


### Developers
  Suyog Barve <suyog.barve@walmart.com>

	
