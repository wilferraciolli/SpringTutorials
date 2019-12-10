# Getting Started

### Active MQ Publish Send Messages
[Tutorial got from here](http://websystique.com/spring/spring-4-jms-activemq-example-with-annotations/) 

##Docker image of active MQ
it can be found on github vromero/docker-activemq
https://github.com/vromero/activemq-artemis-docker

To run, simply do a docker compose up on the docker file under thre resources folder
    docker run -it --rm -p 8161:8161 -p 61616:61616 vromero/activemq-artemis
    
The command above will run a docker image of active MQ
the ports for it are:
    8161 and 61613

Then log into [http://127.0.0.1:8161/](http://127.0.0.1:8161/) with the user defined on the docker-compose file


#Messaging architecture

##ConnectionFactory
In order to create a connection to a messaging provider [message broker], we need connection factories. ConnectionFacotry is basically administrative objects with configurations parameters.

##Persistent/Non-persistent Messages [Aka Delivery Mode : for Messages]
  Delivery Mode refers to persistence/non-persistence of messages which can be specified on MessageProducer level as well as on individual message level. Default delivery mode is PERSISTENT, means messages will be stored on disk/database until it is consumed by a consumer, and will survive a broker restart. When using non-persistent delivery, if you kill a broker then you will lose all in-transit messages.

##Durable / Non-durable subscriptions[for Topics]
  Subscription refers to subscription on a topic. With a durable subscription, if the subscriber [which has subscribed for message on a topic] is down for some time, once it comes up, it will receive all the messages sent for it(including the ones sent when it was down). With Non-durable subscription, a subscriber will receive only the messages when it was connected to topic (will loose all the ones sent when it was down). Note that this is not applicable for Queue’s as they can be considered always durable [only one consumer, and it will always receive the message destined for it in queue].

###JmsTemplate : JmsTemplate provides an abstraction which hides all the complexities of JMS communication. Without JmsTemplate, you will be forced to create connections/sessions/MessageProducers/MessageConsumers and catch all the nasty exception platform may throw. With JmsTemplate ,you get simple API’s to work with , and spring behind-the-scenes take care of all the JMS complexities. It takes care of creating a connection, obtaining a session, and finally sending [as well as synchronous receiving] of message. We will be using JmsTemplate for sending the message. Do note that JmsTemplate also provides possibilities for receiving message but that is synchronous[blocks the listening application], and usually not preferred when asynchronous communication is possible.
   
###   MessageListenerContainer : 
MessageListenerContainer comes handy when we want to implement asynchronous message reception. It can be configured to use a bean [which implements javax.jms.MessageListener] whose onMessage() method will be called on message reception.
