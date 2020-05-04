@echo off
echo ===========================
echo BUILD GRADLE
echo ===========================
call gradlew build

echo ===========================
echo START SERVER
echo ===========================

call java -server -jar build\libs\zsb-db-backend-1.0-SNAPSHOT-all.jar
