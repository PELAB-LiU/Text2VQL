import sys
import os

TEXT2VQL_ROOT = os.path.abspath(os.path.dirname(__file__))

while True:
    if os.path.basename(TEXT2VQL_ROOT) == "Text2VQL":
        sys.path.append(os.path.join(TEXT2VQL_ROOT, "dataset_construction"))
        break
    new = os.path.dirname(TEXT2VQL_ROOT)
    if new == TEXT2VQL_ROOT:
        raise FileNotFoundError("Could not find a parent directory named 'Text2VQL'.")
    TEXT2VQL_ROOT = new