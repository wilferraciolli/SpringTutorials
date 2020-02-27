# Authorization Service

This project shows how to create an authorization service


###To log in use the following url


###To request a token use the following url#]
    POST localhost:8080/oauth/token
    passing the authentication as basic
        username = <clientId>
        password = <clientSecret>
     with the payload of  
        grant_type = password
        username = <username>
        password = <password>
