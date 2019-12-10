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
