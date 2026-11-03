FROM ubuntu:latest

RUN apt update -y

RUN apt install -y sudo
RUN usermod -aG sudo ubuntu 
RUN echo "ubuntu ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

RUN apt update -y
RUN apt install -y wget unzip git nano vim build-essential

# Install java
RUN apt install -y openjdk-21-jdk
# Install maven (gradle is installed by wrapper if needed)
RUN apt install -y maven

# Database setup
RUN apt install -y sqlite3 

# PIP setup
RUN apt install python3-full -y
RUN apt install python3-pip -y

# Virtual env setup
USER root
COPY requirements.txt /opt/requirements.txt 
RUN mkdir -p /opt/venv && chown -R ubuntu:ubuntu /opt/venv /opt/requirements.txt
USER ubuntu
RUN python3 -m venv /opt/venv
RUN /opt/venv/bin/pip install -r /opt/requirements.txt
ENV PATH="/opt/venv/bin:$PATH"

USER ubuntu