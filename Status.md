
We apply and deserve the following badges:
* Artifacts Available. A release of the GitHub repository is published in Zenodo at [https://doi.org/10.5281/zenodo.12742459](https://doi.org/10.5281/zenodo.12742459)
* Artifact Evaluated − Functional. 
    * If the requirements are met (e.g., GPU constraints), everything is runnable. Furthermore, everything is documented in the READMEs.
    * Artifact contains the source code, build scripts, execution sripts, and post processing scripts which if executed, produces results, including figures and logs, that support our claims in the paper. 
    * The artifact makes all dependencies 
* Artifact Evaluated − Reusable. The repository is designed in a way that one can create her own dataset, and train and evaluate her own LLMs. 
    * We provide extensive step-by-step instructions allowing both modifications to each step, as well as extractions of each module to use and/or modify arbitrarily.
    * Project structure is built to isolate parts of the project. 
    * Setting up build environments are made easy by (i) providing setup scripts for the python environment, and (ii) a dedicated docker container for the java/eclipse components. 
    * The artifact includes tips for possible non-deterministic, external issues.

Change log (from kick-the-tires to review):
* Updated Zenodo link (review version) 
* Clarified instructions for setup and configuration requirements
* Modified compilation instruction for Java components
* Docker image is now available on Zenodotea
* Top-level README in now available on Zenodo, outside of the zip file
* Updated to include slf4j-simple in Dockerfile, build.xml, and .classpath
* Script added to help setting up syntax check dependencies for host-side project
