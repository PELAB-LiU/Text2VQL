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

:::danger[Thread pollution]
With the removal of `Thread.stop()` in Java 21 or later, it is not possible to kill uncooperative theads. As a result, the reflectively loaded java queries will run for forever if it contains an infinite loop. Each query claims a worker from the limited (16) threadpool until the program is terminated.

There is a timeout of 30 minutes, and it will yield a semantically incorrect verdict with the indicator of the model that triggered the infinite loop. However, it *cannot* terminate the execution of the java query. If the case completes, then you can just continue. If too many threads are claimed, it is recommended to restart the server and the evaluation. Processed LLM responses are not reevaluated which will limit the impact.

If a test case contains too  many infinite loops, then there is a problem that requires manual intervention.
Locate unchecked queries with infinite loops in the database, and set the `syntax` to `true`, `semantics` to `false`, and `diagnostics` to `infinite loop` (or anything you prefer). This blocks any following evaluation as the query already has a correctness result.

Monitor the CPU usage of the java program and if it utilizes some threads to a 100 percent, (CPU usage is n*100% + few %) and the evaluation seems stuck (no new result for a few minutes), you can assume that there is query with an infinite loop. 
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


