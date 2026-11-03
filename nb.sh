#!/usr/bin/env bash

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

jupyter notebook --notebook-dir="$SCRIPT_DIR"