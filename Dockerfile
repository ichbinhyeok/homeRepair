FROM gradle:8-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Skip tests to speed up build in MVP context
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine
EXPOSE 8081
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
COPY --from=build /home/gradle/src/src/main/jte /jte
ENTRYPOINT ["java","-jar","/app.jar"]
