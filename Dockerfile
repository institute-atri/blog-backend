FROM openjdk:21-slim

WORKDIR /app

COPY target/backend-blog-0.0.1-SNAPSHOT.jar /app/backend-blog.jar

CMD ["java", "-jar", "backend-blog.jar"]