FROM maven:latest as build-stage
COPY . .
RUN mvn clean package

# production stage
FROM amazoncorretto:8 as production-stage
COPY --from=build-stage target/todoapp-srv-1.0.0.jar todoapp-srv-1.0.0.jar
COPY --from=build-stage /www/build.zip /www/build.zip
RUN yum -y install unzip
RUN mkdir -m 755 /www
WORKDIR "/www"
# RUN wget -P /www https://gitlab.com/api/v4/projects/40920491/jobs/artifacts/master/download?job=build&private_token=${GLTOKEN}
RUN unzip /www/build.zip
ENTRYPOINT ["java","-jar","/todoapp-srv-1.0.0.jar"]
