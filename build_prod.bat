@echo off
echo ===========================
echo BUILD GRADLE
echo ===========================
call gradlew build

echo ===========================
echo BUILD DOCKER IMAGE
echo ===========================
call docker build -t zsb-backend .

:: echo ===========================
:: echo SAVE IMAGE TO .tar
:: echo ===========================
:: call docker image save -o cp-mensa.tar cp-mensa-docker

:: echo ===========================
:: echo COPY cp-mensa.tar TO SERVER
:: echo ===========================
:: call scp cp-mensa.tar dev@lwivs43.gm.fh-koeln.de:/var/www/dev.mensa.campus-planer.com