# This Dockerfile is used to build a Docker image for the backend of a blog application.
# It uses the OpenJDK 21 slim base image and copies the backend-blog-0.0.1-SNAPSHOT.jar file
# into the /app directory. The CMD instruction specifies the command to run when the container starts,
# which is to execute the backend-blog.jar file using the Java runtime.

FROM openjdk:21-slim

WORKDIR /app

COPY target/backend-blog-0.0.1-SNAPSHOT.jar /app/backend-blog.jar

CMD ["java", "-jar", "backend-blog.jar"]