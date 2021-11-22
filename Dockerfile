FROM openjdk:15.0.2

RUN mkdir /app
COPY ./build/libs/zsb-db-backend-1.0-SNAPSHOT-all.jar /app/zsb-db-backend-1.0-SNAPSHOT-all.jar
WORKDIR /app
CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "zsb-db-backend-1.0-SNAPSHOT-all.jar"]
