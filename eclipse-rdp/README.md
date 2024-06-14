Parts implemented in java are warapped in the docker container defined here.
The container contains an eclipse environment with the build dependencies and necessary tools to compile the project. Furthermore, it creates a vnc server (web-based remote desktop) that allows the inspection and modification of the source code.

Note that the imeage is set up to use a copy of *refinery* (in /config/refinery) and *eclipse-based code* (in /config/eclipse-workspace/se.liu.ida.sas.pelab.vqlsyntaxcheck).
However, the text2vql folder from the host is mounted to /confic/text2vql.

Build and start docker environemnt. 
```bash
docker build -t eclipse-vnc ../eclipse-rdp
docker run -d \
  --name=eclipse-vnc \
  --security-opt seccomp=unconfined \
  -p 3000:3000 \
  -p 3001:3001 \
  --shm-size="1gb" \
  -v $(pwd)/..:/config/text2vql \
  eclipse-vnc
```

(Optional) Stop and remove container.
```bash
docker container stop eclipse-vnc
docker container rm eclipse-vnc
```


