FROM maven:latest as build-stage
COPY . .
RUN mvn clean package

# production stage
FROM amazoncorretto:8 as production-stage
COPY --from=build-stage target/todoapp-srv-1.0.0.jar todoapp-srv-1.0.0.jar
RUN yum -y install unzip
RUN mkdir -m 755 /www
RUN cd /www
RUN wget https://gitlab.com/api/v4/projects/40920491/jobs/artifacts/master/download?job=build&private_token=${GLTOKEN}
RUN ls
RUN unzip build.zip
RUN ls
ENTRYPOINT ["java","-jar","/todoapp-srv-1.0.0.jar"]
