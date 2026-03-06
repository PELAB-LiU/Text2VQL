---
sidebar_position: 2
---

# Environment setup 

The environemtn is designed around Docker and VSCode devcontainer.

Most of the content can be run using the container image provided for the devcontainer on a normal computer. 
However, for finetuning and prompting LLMs we do not provide an environment.


## Prerequisites 

* A working Docker installation. Follow the official Docker guide for [installation](https://docs.docker.com/engine/install/ubuntu/).
  * Ensure that Docker can be run as a non-root user. See the [post-installation guide](https://docs.docker.com/engine/install/linux-postinstall/).
* A working installation of VS Code is recommended.

:::info
In the rest of this guide, we assume that you are using a terminal inside VS Code (or a Bash shell directly inside the Docker container).
:::

## Project Structure

The project is organized as follows:
* `.devcontainer`: Contains files for Docker and VS Code. This includes the Dockerfile for the development environment (excluding the LLM fine-tuning and prompting setup) and the necessary configuration files for VS Code to set up the devcontainer.
* `dataset_construction`: Responsible for processing domain models, generating prompts for ChatGPT, executing the prompts, parsing the responses, running syntax checks, and generating the datasets.
* `finetuning`: Contains scripts and configurations for fine-tuning and prompting open-source LLMs.
* `java`: Contains the server used for syntactic and semantic evaluation, as well as tools for generating test models. It also includes the project used to generate Java code from EMF Ecore models.
* `results`: Contains the files required to process and analyze the experimental results reported in the paper.

Auxiliary Directories:
* `.github`: Repository-related configuration files (e.g., CI workflows). Not required for using the project.
* `gh-pages`: Web-based project documentation built using the Docusaurus framework.