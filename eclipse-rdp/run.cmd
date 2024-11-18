docker run -d ^
  -v \\wsl$\Ubuntu\tmp\.X11-unix:/tmp/.X11-unix ^
  -v \\wsl$\Ubuntu\mnt\wslg:/mnt/wslg ^
  --name=text2vql ^
  --security-opt seccomp=unconfined ^
  -p 4050:3389 ^
  --shm-size="1gb" ^
  -v %cd%/../dataset_construction:/config/dataset_construction ^
  text2vql