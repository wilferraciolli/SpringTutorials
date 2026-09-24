FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 5001
# Memory ergonomics for small free-tier containers (512 MB). Without these the
# JVM can misread the limit and set a heap that OOM-kills the container on boot
# (seen on Fly's 256 MB machines). SerialGC is the right collector for <=1 vCPU
# and a small heap. Override JAVA_TOOL_OPTIONS at runtime for bigger hosts.
# For a genuinely tiny / zero-cost target, build the native image instead --
# see Dockerfile.native.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65.0 -XX:+UseSerialGC -Xss512k -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java", "-jar", "app.jar"]
