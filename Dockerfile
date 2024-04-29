FROM openjdk:23-slim

COPY target/backend-blog-0.0.1-SNAPSHOT.jar /app/backend-blog.jar

WORKDIR /app

CMD ["java", "-jar", "backend-blog.jar"]