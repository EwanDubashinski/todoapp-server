FROM maven:latest as build-stage
COPY . .
RUN mvn clean package

# production stage
FROM amazoncorretto:8 as production-stage
COPY --from=build-stage target/todoapp-srv-1.0.0.jar todoapp-srv-1.0.0.jar
COPY --from=build-stage --chmod=777 updateFrontend.sh updateFrontend.sh
RUN yum -y install unzip
RUN mkdir -m 755 /www

CMD updateFrontend.sh

ENTRYPOINT ["java","-jar","/todoapp-srv-1.0.0.jar"]
