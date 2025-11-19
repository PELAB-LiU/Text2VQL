import shutil
import os
import subprocess

def setup_env(sample, projectroot):
    cluster, model = sample
    target = projectroot / "modules/model/model/model.ecore"
    if os.path.exists(target):
        os.remove(target) 
    shutil.copy(cluster, projectroot / "modules/model/model/model.ecore")

def attempt_compile(projectroot):
    result = subprocess.run(
            ["mvn", "clean", "package"],
            env={"ECORE_PATH":"modules/model/model/model.ecore"},
            cwd=projectroot,
            text=True,
            check=False
        )
    return result.returncode

#def compile()
#    with 