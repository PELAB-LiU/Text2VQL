from openai import OpenAI
import pickle

if __name__ == "__main__":
    batchfile = "file-KpMLZ7bbxxfQT4uZ9XUZxh"
    client = OpenAI()

    batchobject = client.batches.create(
        input_file_id=batchfile,
        endpoint="/v1/responses",
        completion_window="24h",
        metadata={
            "description": "Test run for Text2VQL barch generation."
        }
    )
    with open(f"logs/batchobj_{batchobject.id}.pkl", "wb") as f:
        pickle.dump(batchobject, f)