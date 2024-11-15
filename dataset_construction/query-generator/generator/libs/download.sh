#!/bin/bash

cd "$(dirname "$0")"

# https://download.eclipse.org/staging/2024-12/buildInfo/archive/download.eclipse.org/staging/2024-12/index/org.eclipse.ocl_3.22.0.v20240902-1518.html
wget https://download.eclipse.org/staging/2024-12/plugins/org.eclipse.ocl_3.22.0.v20240902-1518.jar -O ocl.jar
wget https://download.eclipse.org/staging/2024-12/plugins/org.eclipse.ocl.ecore_3.22.0.v20240902-1518.jar -O ocl.ecore.jar
wget https://download.eclipse.org/staging/2024-12/plugins/org.eclipse.ocl.common_1.22.0.v20240902-1518.jar -O ocl.common.jar