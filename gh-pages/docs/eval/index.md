---
sidebar_position: 5
---

# Evaluation

Evaluation follows a server-client architecture.
The `java/generator` project provides a gradle task `:server:startServer` task that starts an HTTP server capable of receiving compatison tasks and respond with the comparison result.
A python script in the `text2vql` module is designed to call the server for queries in the evaluation database.

## Start server

Open a new bash terminal.

Change directory to `java/generator`.

Start the server.

```bash
./gradlew :server:startServer
```

:::info[Startup time]
It can take minutes to compile and start the full code. If the code is compiled it can take up to 30 seconds to start the server.

You will see a message that the server is started. Wait for this message before proceeding further.
:::

## Start evaluation

An evaluation can take from a few seconds to hours, depending on the number of LLM responses to evaluate, number of test instance models and correctness of the LLM responses.

:::info[Working directory]
`dataset_construction`
:::


:::warning[Networking]
Ensure that the server is visible for the python script.
:::

```bash
python3 -m text2vql.evaluation.evaluate --db ../finetuning/evaluation.db
```

:::info[Run for one test case only]
Provide an `--id <test case id>` in the command above.
:::


