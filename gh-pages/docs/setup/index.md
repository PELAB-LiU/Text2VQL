---
sidebar_position: 2
---

# Environment setup 

The environemtn is designed around Docker and VSCode devcontainer.

Most of the content can be run using the container image provided for the devcontainer on a normal computer. 
However, for finetuning and prompting LLMs we do not provide an environment.


## Install docker 

* [Docker engine](https://docs.docker.com/engine/install/ubuntu/).
  * Ensure that you can run docker as a non-root user. See [post-installation guide](https://docs.docker.com/engine/install/linux-postinstall/).

:::info
Configuring sudo for docker may be omitted if you run the commands directly inside the container.
:::

:::info
In the rest of the guide, we assume that you use a terminal inside VSCode (or use a bash sell directly in dhe docker container).
:::

## Install Visual Studio Code

:::danger
TODO installation guide
:::