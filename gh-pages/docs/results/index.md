---
sidebar_position: 6
---

# Results

:::info[Working directory]
This pahse can be started from any directory.
* You are recommended to have a browser. (If not, the container has (?) Firefox in it, but you need to mount a graphics environment to the container. VSCode on Windows automatically mounts a GUI via WSL.)
* The environemnt opens on port `8888`. (VSCode automaically forwards the port if the jupyter nevironment is started from an integrated terminal.)
:::

The results are processed in a jupyter notebook. Start the notebook with the following command:

```bash
jupyter notebook --notebook-dir=/workspaces/Text2VQL/results/
```

