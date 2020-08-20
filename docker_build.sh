#!/bin/sh

img_name=zsb-backend
imgs_name=${img_name}-images.tar

buildApp() {
  ./gradlew build
}

buildDockerImage() {
  docker image rm ${img_name}
  docker build -t ${img_name} .
}

packDockerImages() {
  echo packing images...
#  docker save -o ${imgs_name} $(docker-compose config | awk '{if ($1 == "image:") print $2;}' ORS=" ")
#  echo images packed
}

clearDockerImages() {
  docker-compose stop &&
    docker-compose down &&
    docker image rm ${img_name} &&
    docker image prune -f
}

deployDockerImages() {
  # docker load -i ${imgs_name} &&
    docker-compose up -d
}

uploadToDevServer() {
  echo uploading to dev server...
#  scp ${imgs_name} dev@lwivs43.gm.fh-koeln.de:/home/dev/${img_name} &&
#    rm ${imgs_name} &&
#    echo images uploaded
}

case "$1" in
"stage")
  clearDockerImages &&
    buildApp &&
    buildDockerImage &&
    packDockerImages &&
    uploadToDevServer
  ;;
"run")
  deployDockerImages
  ;;
*)
  echo expected stage or run, but was $1
  exit 1
  ;;
esac
