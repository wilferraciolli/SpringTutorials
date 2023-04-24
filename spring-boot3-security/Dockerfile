FROM ibm-semeru-runtimes:open-17-jre-focal
ADD target/spring-boot3-security-0.0.1-SNAPSHOT.jar /app/server.jar
ENV PORT 5001
EXPOSE 5001

CMD java -XX:MaxRAM=70m -jar /app/server.jar
