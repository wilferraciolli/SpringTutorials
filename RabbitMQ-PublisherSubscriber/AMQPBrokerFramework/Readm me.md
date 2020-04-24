This library offers common messaging infra structure


It needs adding the component, jpaRepo and Component Scans to the Spring Boot application


It requires the following entities and tables

    * MessageReceived
    * MessageSent

This library creates some kind on auditing for when receiveng and sending messages

This library works out message types by the @jsonRootName annotation
This library works out whether to send to fanout or direct exchange by the presence of the @DirectExchange annotation
