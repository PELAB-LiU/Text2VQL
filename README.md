# Text2VQL: Teaching a Model Query Language to Open-Source Language Models with ChatGPT

This is the codebase of the paper "Text2VQL: Teaching a Model Query Language to Open-Source Language Models with ChatGPT".

## Repository structure

![](./figures/text2vql-1.png)

The repository is structured as follows:
* The `dataset_construction` contains all the procedures to generate the synthetic dataset of VQL-NL pairs.
* The `training` folder contains all the scripts to fine-tune open-source LLMs using the synthetic dataset.
* The `results` contains the results presented in the paper and the scripts needed to execute the testing framework
of the paper.
* The `eclipse-rdp` folder contains the docker set-up for the java scripts.

## Running everything
To reproduce the full paper, the order is the following:
1. Run the scripts of `eclipse-rdp` to build the docker image.
2. Run the scripts of `dataset_construction`.
3. Run the scripts of `training`.
4. Run the scripts of `results`.

For each phase/folder, you have to move the working directory to the associated folder (e.g., `cd eclipse-rdp`).
Each phase/folder has its own `README.md` explaining all the requirements and steps.
We assume that you are using Ubuntu.
The `results` folder contains not only the scripts used to answer all the RQs but also
the data associated to the paper. Therefore, if you start running all the scripts from the very first phase,
you will overwrite eventually all this data.
