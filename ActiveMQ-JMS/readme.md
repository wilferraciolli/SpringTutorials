# Active MQ JMS project

This is a sample application that implements the Java JMS spec to send/receive data.


####A message consists of
    Header - CorrelationId, exporation, messageId, timestamp, priority,Type, replyTo, redelivery, persistent
    Properties - UserId, appId, GroupId, provier/consumer TransacrionId and custon properties
        Application
        Provider
        Standard Properties
     Payload

####Message type
    Text, Byte, Strean...
    
    
##Docker image of active MQ
it can be found on github vromero/docker-activemq
https://github.com/vromero/activemq-artemis-docker

To run, simply follow the guides
    docker run -it --rm -p 8161:8161 -p 61616:61616 vromero/activemq-artemis
    
The command above will run a docker image of active MQ
the ports for it are:
    8161 and 61613

Then log into http://127.0.0.1:8161/ with the user
    user - artemis
    password - simetraehcapa


If you wish to change the default username and password of artemis / simetraehcapa, you can do so with the ARTEMIS_USERNAME and ARTEMIS_PASSWORD environment variables:

docker run -it --rm \
  -e ARTEMIS_USERNAME=myuser \
  -e ARTEMIS_PASSWORD=otherpassword \
  vromero/activemq-artemis
