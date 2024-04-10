# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.wiltech.config-server' is invalid and this project uses 'com.wiltech.configserver' instead.

# Getting Started


echo "Assuming that docker-compose and vault CLI are installed"
echo "If not check https://docs.docker.com/compose/install/ for Docker Compose installation"
echo "and https://www.vaultproject.io/docs/install for Vault CLI"

# Run Vault
docker run \
-d \
-p 8200:8200 \
-e 'VAULT_DEV_ROOT_TOKEN_ID=vault-plaintext-root-token'\
-e 'VAULT_ADDR=http://0.0.0.0:8200' \
vault
echo "Waiting for Vault to start..."

echo "Add entries to vault"
export VAULT_ADDR='http://127.0.0.1:8200'
export VAULT_TOKEN='vault-plaintext-root-token'

echo "Adding a property for all applications (same as application.yml)"
vault kv put secret/application only.in.vault="Hello"
echo "Adding a property for app bar with profile foo"
vault kv put secret/bar,foo a="bar with foo profile value from Vault"
echo "Reading the properties back"
vault kv get secret/application
vault kv get secret/bar,foo

echo "Fetch config server with vault configuration"
curl https://start.spring.io/starter.tgz -d dependencies=cloud-config-server,cloud-starter-vault-config \
-d language=java -d type=maven-project -d baseDir=spring-cloud-config-server | tar -xzvf -

pushd spring-cloud-config-server
chmod +x mvnw

     echo "Set up Controllers, Services & properties etc. Check the snippets above"

     ./mvnw clean install

     echo "Running the application"
     java -jar target/*.jar

     echo "Waiting for the application to start"

     echo "Encrypting [my secret] text"
     curl localhost:8888/encrypt -d "my secret"
     # got back response e.g. "a92419f589f28667fda8bea88ae3ee50deb2c38aca326ae064c6f430e4666115"
     curl localhost:8888/decrypt -d "a92419f589f28667fda8bea88ae3ee50deb2c38aca326ae064c6f430e4666115"
     # got back response "my text"

     pushd ~/yourGitFork
         echo "Appending ciphered entry to bar.properties"
         echo "encrypted={cipher}a92419f589f28667fda8bea88ae3ee50deb2c38aca326ae064c6f430e4666115" >> bar.properties
         git commit -am "Encryption"
         git push origin main
     popd

     echo "Restarting the Config Server application"

     echo "Question: Which property sources were used to find the ultimate properties for application "bar" with profile "foo""
     curl localhost:8888/bar/foo/ -H "X-Config-Token: vault-plaintext-root-token" | jq
     echo "Question: What will be the final properties configuration for application `bar` with profile `foo`?"
     curl localhost:8888/bar-foo.properties -H "X-Config-Token: vault-plaintext-root-token"

     # You should get something like this

     #a: bar with foo profile value from Vault
     #config.property: bar with foo profile value for config prop from git
     #only.in.git: hello
     #logging.level.com.example: TRACE
     #encrypted: my secret
     #only.in.vault: Hello
popd
popd