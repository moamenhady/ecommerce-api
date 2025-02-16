FROM eclipse-temurin:21-jre

LABEL authors="moamenhady <moamenhady@outlook.com>"

WORKDIR /opt/app/

COPY env.yml target/ecommerce-api-0.0.1-SNAPSHOT.jar /opt/app/

EXPOSE 8080

CMD ["java", "-jar", "ecommerce-api-0.0.1-SNAPSHOT.jar"]