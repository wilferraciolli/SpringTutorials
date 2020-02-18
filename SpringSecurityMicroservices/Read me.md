# Getting Started

### Reference Documentation
Tutorial showing how to set up
Gateway Service
Service discovery
Spring Security for Microservices

Maven modular applications


This application consists of libraries and microservices all together
It shows how to create, sign and validate tokens for microservices behind an edge Server.

There is a database MySQL available, which is a docker image and is defined on the stack.yaml file.


#The steps to apply security
1 - Create a configuration file to hold details on the token
2 - Create a class to define what end points should be protected
3 - Create a filter to validate the token 
4 - The filter will, authenticate the user, generate the token, sign it and encrypt



PS
