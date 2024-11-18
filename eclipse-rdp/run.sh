#!/bin/bash

docker run -d \
  --name=text2vql \
  --security-opt seccomp=unconfined \
  -p 4050:3389 \
  --shm-size="1gb" \
  v $(pwd)/..:/config/text2vql \
  text2vql