# Heroku test app

more info can be found here https://www.callicoder.com/deploy-host-spring-boot-apps-on-heroku/

This app shows how to deploy a spring boot app to the cloud using the heroku maven plugin

Prereqs
     add the default port to the application properties
         ```server.port=${PORT:8080}```
     run the application from the terminal prefixing it the heroku api key
         ```HEROKU_API_KEY=8a664a2f-503d-49fc-85cb-eddf7e0c18d6 mvn clean heroku:deploy```
     
To check for logs use (ps needs heroku cli installed)
    ```heroku logs --tail --app heroku-test1985```