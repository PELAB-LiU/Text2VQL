# Building docker environment for Eclipse and Refinery

## General description
Parts implemented in Java are wrapped in the docker container defined here.
The container contains an eclipse environment with the build dependencies and necessary tools to compile the project. 

Furthermore, it creates a VNC server (web-based remote desktop) that allows the inspection and modification of the source code. It can be accessed in a browser of your choice via [http://localhost:3000](http://localhost:3000).
Code editors are available under *Applications* -> *Development* -> *[Modeling 2024-03 | IntellijJ]*.
* **Modeling 2024-03** is an eclipse instance to inspect, edit, and compile the code for syntax check, query complexity check, and query match comparison check. To inspect the code, open the default (`/config/eclipse-workspace`) workspace.
* **IntelliJ** is there to inspect and edit the code of refinery modified for test model generation. To inspect the code, open the `/config/refinery` directory.

Note that the image is set up to use a copy of *refinery* (in /config/refinery) and *eclipse-based code* (in /config/eclipse-workspace/se.liu.ida.sas.pelab.vqlsyntaxcheck).
However, the Text2VQL folder from the host is mounted to /config/text2vql. To make permanent changes to the code, you can open refinery or the eclipse workspace in `/config/text2vql/eclipse-rdp/[refinery|eclipse-workspace]`. 

## Build, Run, and Stop.

To build an image, run the following command. This will create the `eclipse-vnc` image.
```bash
docker build -t eclipse-vnc .
```

Future commands will be executed as user `abc` from the container to avoid permission conflicts with the remote environment.
Make sure that this does not conflict with host permissions for the `Text2VQL` directory, otherwise commands may fail to save their outputs.
The most basic solution is to execute the following command.
```bash
chmod -R o+rw ..
```

To create a container run the following command. This will start the container, including the VNC server. 
```bash
docker run -d \
  --name=eclipse-vnc \
  --security-opt seccomp=unconfined \
  -p 3000:3000 \
  -p 3001:3001 \
  --shm-size="1gb" \
  -v $(pwd)/..:/config/text2vql \
  eclipse-vnc
```

(Delayed) When you don't need the container anymore, you can stop and remove it with the following command.
```bash
docker container stop eclipse-vnc
docker container rm eclipse-vnc
```

If for any reason, you prefer to use RDP instead of VNC, you can change the base image in the Dockerfile from webtop to rdesktop. Do not forget to change the ports accordingly.
Click the links for further documentation regarding [webtop](https://docs.linuxserver.io/images/docker-webtop/) or [rdesktop](https://docs.linuxserver.io/images/docker-rdesktop/).

Notes: 
* If downloading eclipse in `RUN wget 'https://www.eclipse.org/downloads/...` is proceeding slowly, then kill and restart the build. It has a good chance to help.
* When connecting to the container via VNC, it may exhibit a clipboard permission error. You may close the error message, as this error does not affect the operation of the container. This is an [open issue](https://github.com/kasmtech/KasmVNC/issues/219).
* Default user is *abc* with password *abc*. Home directory of abc is `/config`.
