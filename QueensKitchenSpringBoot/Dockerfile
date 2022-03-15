FROM openjdk:11

LABEL maintainer="mayankit3@gmail.com"

VOLUME /main-app

ADD build/libs/QueensKitchenSpringBoot-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar","/app.jar"]