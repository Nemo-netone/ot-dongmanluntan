FROM maven:3.9.9-eclipse-temurin-8 AS build

WORKDIR /app
COPY pom.xml .

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:8-jre

WORKDIR /app
ENV PORT=8090
COPY --from=build /app/target/sui-springboot.jar app.jar

EXPOSE 8090
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8090} -jar app.jar"]
