# If changed, update the container repository manually with:
# docker login gitlab.liu.se:5000
# docker build --provenance=false -t gitlab.liu.se:5000/pelab/pelab.gitlab-pages.liu.se:[YOUR-TAG] .
# docker push gitlab.liu.se:5000/pelab/pelab.gitlab-pages.liu.se:[YOUR-TAG]

FROM ubuntu:latest
RUN useradd -m abc

RUN apt update -y
RUN apt install -y wget unzip git

#Install java
RUN apt install -y openjdk-21-jdk
# Install maven
RUN apt install -y maven
#Install gradle
# Wrapper installs it?

# Database setup
RUN apt install -y sqlite3 


# PIP setup

RUN apt install python3-full -y
RUN apt install python3-pip -y

# Virtual env setup
USER root
RUN python3 -m venv /opt/venv
RUN chown -R abc:abc /opt/venv
COPY requirements.txt /opt/requirements.txt 

#RUN chmod -R 755 /opt/venv

RUN . /opt/venv/bin/activate && pip install -r /opt/requirements.txt



USER abc