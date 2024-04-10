# Getting Started

### Reference Documentation

## Labs

#### Assingment 1 - Actuator
echo "What is the default logging level for <org.apache.catalina.util.LifecycleBase>?"
curl http://localhost:4321/actuator/loggers/org.apache.catalina.util.LifecycleBase

echo "Which endpoint ensures that your application is healthy?"
curl http://localhost:4321/actuator/health

echo "Which Java Home variable does your application take into consideration (it's there as a JAVA_HOME env var)"
curl http://localhost:4321/actuator/env/JAVA_HOME

echo "Why wasn't the R2dbcAutoConfiguration read?"
curl http://localhost:4321/actuator/conditions | jq '.contexts.application.negativeMatches.R2dbcAutoConfiguration'

#### Assingment 2 - Spring Cloud and Refresh Scope
echo "In case of using the previous project you just need to update the BOM and add the spring-cloud-starter dependency. Check the pom.xml above for details"

echo "Assuming that we need a fresh project from start.spring.io"
curl https://start.spring.io/starter.tgz -d dependencies=web,data-jpa,actuator,h2,cloud-starter \
-d language=java -d type=maven-project -d baseDir=spring-cloud | tar -xzvf -

pushd spring-cloud
chmod +x mvnw

    echo "Set up Controllers, Services & properties etc. Check the snippets above"

    echo "Generating the JAR"
    ./mvnw clean install

    pushd target
      echo "Creating an empty application-foo.properties file"
      touch application-foo.properties

      echo "Running the application at port 7654 with profile foo"
      java -jar *.jar --spring.profiles.active=foo --server.port=7654

      echo "Waiting for the application to start at port 7654..."

      echo "Send 3  requests - should return <test>, <test> and <default> values and no logging"
      curl localhost:7654/refreshed
      curl localhost:7654/nonrefreshed
      curl localhost:7654/configprop

      echo "Change the contents of the file - check the code above for application-foo.properties"

      echo "Send 3  requests - nothing should change - same values, no logging"
      curl localhost:7654/refreshed
      curl localhost:7654/nonrefreshed
      curl localhost:7654/configprop

      echo "Send post to do refresh"
      curl -X POST localhost:7654/actuator/refresh

      echo "Send 3  requests - should return <new value>, <test>, <new value for config prop>"
      echo "Also DEBUG, INFO and WARN logs should be there in the application output"
      curl localhost:7654/refreshed
      curl localhost:7654/nonrefreshed
      curl localhost:7654/configprop

      echo "The /nonrefreshed endpoint will return <test> because the service bean that returns the value has not been refreshed. It still has the String value of the original, default property <test>"

    popd
popd