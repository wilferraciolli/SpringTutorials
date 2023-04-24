# spring boot 3 security
This is a sample application to show how to use security within SB3, and it also uses a native image Grall VM


The tutorial can be found here `https://github.com/ali-bouali/spring-boot-3-jwt-security` and the video here 
`https://www.youtube.com/watch?v=KxqlJblhzfI`

# Building the image
`Run mvn spring-boot:build-image` this will build the docker image for the graal VM.
The output of the build image will return the newly created Docker image.

Then to run the container, type in 
    `docker run -p 5001:5001 spring-boot3-security:0.0.1-SNAPSHOT`


# Deploy to fly.io
PS First make sure the FlyCtrl is installed. 
`curl -L https://fly.io/install.sh | sh` to install 
`export FLYCTL_INSTALL="/home/wilferraciolli/.fly"` to set the paths
`export PATH="$FLYCTL_INSTALL/bin:$PATH" to set th epaths
`
First login `/home/wilferraciolli/.fly/bin/flyctl auth login`

Then create the app `/home/wilferraciolli/.fly/bin/flyctl launch` - This will create an app in Fly IO then create the fly.toml file with config

Then create the image from the Docker file (optional)
`docker build -t registry.fly.io/spring-boot3-security:0.0.1-SNAPSHOT .`

Then push the image build earlier to the Fly IO registry
`/home/wilferraciolli/.fly/bin/flyctl auth docker`
Then tag the image with the fly io docker registry
`docker tag b3eae6890dc9 registry.fly.io/spring-boot3-security:0.0.1-SNAPSHOT`
Then push the image
`docker push registry.fly.io/spring-boot3-security:0.0.1-SNAPSHOT`
Finally deploy the image
`/home/wilferraciolli/.fly/bin/flyctl deploy --app spring-boot3-security \
--image registry.fly.io/spring-boot3-security:0.0.1-SNAPSHOT`









# H2 console database
To see the database on the browser
disable authentication
then add the folowing properties on the properties file
#Add h2 database connection details
spring.jpa.hibernate.ddl-auto=none
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:file:~/test
spring.datasource.driver-class-name=org.h2.Driver

Then go the the page `http://localhost:5001/api/h2-console` and log in - make sure that the path is set to jdbc:h2:mem:database



PS this is a template for Spring boot 3 security and using Graal VM as deploymnet for native Spring






# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.5/maven-plugin/reference/html/#build-image)
* [GraalVM Native Image Support](https://docs.spring.io/spring-boot/docs/3.0.5/reference/html/native-image.html#native-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#web)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Spring Security](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#web.security)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)

### Additional Links
These additional references should also help you:

* [Configure AOT settings in Build Plugin](https://docs.spring.io/spring-boot/docs/3.0.5/maven-plugin/reference/htmlsingle/#aot)

## GraalVM Native Support

This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

### Lightweight Container with Cloud Native Buildpacks
If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:

```
$ ./mvnw spring-boot:build-image -Pnative
```

Then, you can run the app like any other container:

```
$ docker run --rm -p 8080:8080 spring-boot3-security:0.0.1-SNAPSHOT
```

### Executable with Native Build Tools
Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

NOTE: GraalVM 22.3+ is required.

To create the executable, run the following goal:

```
$ ./mvnw native:compile -Pnative
```

Then, you can run the app as follows:
```
$ target/spring-boot3-security
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run your existing tests in a native image, run the following goal:

```
$ ./mvnw test -PnativeTest
```

