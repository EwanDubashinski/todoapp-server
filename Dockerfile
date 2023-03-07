FROM amazoncorretto:8
COPY target/todoapp-srv-1.0.0.jar todoapp-srv-1.0.0.jar
ENTRYPOINT ["java","-jar","/todoapp-srv-1.0.0.jar"]