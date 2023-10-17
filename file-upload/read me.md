# Spring Boot file CRUD example

### This is a tutorial from 
`https://www.bezkoder.com/spring-boot-upload-file-database/`
and 
`https://www.techgeeknext.com/angular-upload-image-spring-boot-example`


### Additional resources
`https://www.techgeeknext.com/angular-upload-image-spring-boot-example`





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