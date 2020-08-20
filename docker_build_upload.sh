#!/bin/sh

img_name=zsb-backend
packed_img_name=${img_name}.tar
# imgs_name=all-images.tar

buildApp() {
  ./gradlew build
}

buildDockerImage() {
  docker image rm ${img_name}
  docker build -t ${img_name} .
}

packBackend() {
  echo packing image...
  # docker save -o ${imgs_name} $(docker-compose config | awk '{if ($1 == "image:") print $2;}' ORS=" ")
  docker save -o ${packed_img_name} ${img_name}
  echo image packed
}

clearDockerImages() {
  docker-compose stop &&
    docker-compose down &&
    docker image rm ${img_name}
    docker image prune -f
}

deployDockerImages() {
  docker load -i ${packed_img_name} &&
    docker-compose up -d
}

uploadToDevServer() {
  echo uploading to dev server...
  scp ${packed_img_name} $1 &&
  rm ${packed_img_name} &&
  echo image uploaded
}

case "$1" in
"stage")
  clearDockerImages &&
    buildApp &&
    buildDockerImage &&
    packBackend &&
    uploadToDevServer $2
  ;;
"run")
  deployDockerImages
  ;;
*)
  echo expected stage or run, but was $1
  exit 1
  ;;
esac
