FROM eclipse-temurin:17-jre

WORKDIR /app

COPY user-service/build/libs/user-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENV JWT_PRIVATE_KEY_PATH=file:/app/keys/private.pem
ENV JWT_PUBLIC_KEY_PATH=file:/app/keys/public.pem

ENTRYPOINT ["java", "-jar", "app.jar"]

