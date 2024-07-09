FROM gradle:8.5.0-jdk21 AS build

RUN apt-get update && apt-get install -y curl unzip
ARG GRADLE_VERSION=8.5

RUN curl -sL https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-all.zip -o gradle-${GRADLE_VERSION}-all.zip && \
    unzip gradle-${GRADLE_VERSION}-all.zip -d /opt && \
    rm gradle-${GRADLE_VERSION}-all.zip

ENV GRADLE_HOME=/opt/gradle-${GRADLE_VERSION}
ENV PATH=${GRADLE_HOME}/bin:${PATH}

WORKDIR /app

COPY . .
RUN gradle build --no-daemon

FROM openjdk:21

WORKDIR /app/zsb-backend
COPY --from=build /app/build/libs/zsb-db-backend-all.jar /app/zsb-backend/zsb-db-backend-all.jar
COPY --from=build /app/src/main/resources/legacy_import/data-import.csv /app/zsb-backend/data-import.csv
COPY --from=build /app/src/main/resources/files/serialletter-template.docx /app/zsb-backend/serialletter-template.docx

ENV LETTER_PATH=/app/zsb-backend/serialletter-template.docx

EXPOSE 9000

CMD ["java", "-jar", "zsb-db-backend-all.jar"]
