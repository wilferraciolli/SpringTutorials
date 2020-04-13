This project shows how to use Rabbit MQ to send and receive messages. It shows how to create and subscribe to Exchange and Queues using the following techniques:
    * One-to-Many (publish to every subscriber)
    * One-To-One (use of routing key)
 
The code is designed to have one single message receiver that will read the message, check the type and if the consumer is configured to receive message of that type, it will serialize the object onto a POJO and publish as Domain event, which makes easier to handle.
The idea is to use the messaging to represent an event EG useCreated, userUpdated...

Pre requirements
    Create the database image
    Create the RabbitMQ image
    Build the Messaging framework library within the root of the project
    Create the SQL for the
        *  messagePersisted table
        *  messageReceived table

The rabbitMQ can be started with `docker-compose up` 
It can be accessed on the url http://localhost:15672
The default username and password is `guest` `guest`

This application uses a docker compose file which will start a database and rabbit mq

The code in here is a sample to see how to publish and handle messages
It also creates a record for every published and received message which requires two entities to be created

