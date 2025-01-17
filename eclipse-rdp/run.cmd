docker run -d ^
  --name=text2vql ^
  --security-opt seccomp=unconfined ^
  -p 4100:3389 ^
  --shm-size="1gb" ^
  -v %cd%/..:/config/text2vql ^
  text2vql