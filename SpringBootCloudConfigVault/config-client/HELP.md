# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.wiltech.config-client' is invalid and this project uses 'com.wiltech.configclient' instead.

# Getting Started

curl https://start.spring.io/starter.tgz -d dependencies=cloud-config-client,actuator,web \
-d language=java -d type=maven-project -d baseDir=spring-cloud-config-client | tar -xzvf -

pushd spring-cloud-config-client
chmod +x mvnw

     echo "Set up Controllers, Services & properties etc. Check the snippets above"

     ./mvnw clean install

     echo "Running the application"
     java -jar target/*.jar

     echo "Waiting for the application to start..."

     curl localhost:7654/refreshed
     # you should get: "bar with foo profile value from Vault"
     curl localhost:7654/configprop
     # you should get: "bar with foo profile value for config prop from git"
     curl localhost:7654/encrypted
     # you should get: "my secret"
popd
