# Building docker environment for Eclipse and Refinery

## General description
Parts implemented in Java are wrapped in the docker container defined here.
The container contains an eclipse environment with build dependencies and necessary tools to compile the project. 

Furthermore, it creates a VNC server (web-based remote desktop) that allows the inspection and modification of the source code. It can be accessed in a browser of your choice via [http://localhost:3000](http://localhost:3000).
Code editors are available under *Applications* -> *Development* -> *[Modeling 2024-03 | IntellijJ]*.
* **Modeling 2024-03** is an eclipse instance to inspect, edit, and compile the code for syntax check, query complexity check, and query match comparison check. To inspect the code, open the default (`/config/eclipse-workspace`) workspace. Note that in later steps, the commands use an ANT build script that needs to be exported via Eclipse, if the program entry point changes.
* **IntelliJ** is there to inspect and edit the code of refinery modified for test model generation. To inspect the code, open the `/config/refinery` directory. Build and run are done by  *gradle*.

Note that the image is set up to use a copy of *refinery* (in /config/refinery) and *eclipse-based code* (in /config/eclipse-workspace/se.liu.ida.sas.pelab.vqlsyntaxcheck).
However, the Text2VQL folder from the host is mounted to /config/text2vql. To make permanent changes to the code, you can open refinery or the eclipse workspace in `/config/text2vql/eclipse-rdp/[refinery|eclipse-workspace]`. 

## Build, Run, and Stop.

Change directory to eclipse-rdp.
```bash
cd eclipse-rdp
```

### Build your own image
To build an image, run the following command. This will create the `eclipse-vnc` image.
```bash
docker build -t eclipse-vnc .
```

### Import image
Alternatively, you can import the docker image from [Zenodo](https://doi.org/10.5281/zenodo.12742459).
```bash
docker image load -i <file location>/eclipse-vnc.tar.gz
```

### Run container

Future commands will be executed as user `abc` from the container to avoid permission conflicts with the remote environment.
Make sure that this does not conflict with host permissions for the `Text2VQL` directory, otherwise commands may fail to save their outputs.
The most basic solution is to execute the following command.
```bash
chmod -R o+rw ..
```

To create a container run the following command. This will start the container, including the VNC server. If you imported the `eclipse.vnc` image, replace `$(pwd)/..` with the location of this repository.
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
* Changes made to *xtend* files are ignored in future docker commands, as the java code generation is not automated. You can use the provided Eclipse IDE to make changes, and on save, it will compile the xtend files and update the generated java files.
* Changes to run configuration (e.g., add or relocate main functions) may require changes in the ant build script. (build.xml)
* If downloading eclipse in `RUN wget 'https://www.eclipse.org/downloads/...` is proceeding slowly, then kill and restart the build. It has a good chance to help. (Expected download time is up to 5 minutes.)
* When connecting to the container via VNC, it may exhibit a clipboard permission error. You can safely close the error message, as this error does not affect the operation of the container. This is an [open issue](https://github.com/kasmtech/KasmVNC/issues/219).
* **Login credentials:** default user is *abc* with password *abc*. Home directory of abc is `/config`.

