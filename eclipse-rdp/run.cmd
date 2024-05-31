docker run -d ^
  --name=eclipse-vnc ^
  --device=/dev/dri:/dev/dri ^
  --security-opt seccomp=unconfined ^
  -p 3000:3000 ^
  -p 3001:3001 ^
  --shm-size="1gb" ^
  -v %cd%/../dataset_construction:/config/dataset_construction ^
  eclipse-vnc