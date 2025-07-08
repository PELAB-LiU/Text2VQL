#!/bin/bash

# Exit on error
set -e

# Check if folder argument is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <folder>"
    exit 1
fi

INPUT_DIR="$1"

# Check if folder exists
if [ ! -d "$INPUT_DIR" ]; then
    echo "Error: Folder '$INPUT_DIR' does not exist."
    exit 1
fi

# Create a directory for collected jars
OUTPUT_DIR="domains"
mkdir -p "$OUTPUT_DIR"

# Get all .ecore files in the input folder
ecoreFiles=("$INPUT_DIR"/*.ecore)

# Check if any .ecore files were found
if [ ! -e "${ecoreFiles[0]}" ]; then
    echo "No .ecore files found in $INPUT_DIR."
    exit 0
fi

for ECORE_PATH in "${ecoreFiles[@]}"; do
    echo "Processing $ECORE_PATH..."

    # Copy ECORE file to modules/model/model/model.ecore
    destinationPath="modules/model/model/model.ecore"
    cp -f "$ECORE_PATH" "$destinationPath"
    echo "Copied $ECORE_PATH to $destinationPath"

    # Set environment variable
    export ECORE_FILE="$ECORE_PATH"

    # Run Maven package and capture the result
    echo "Running Maven build..."
    mvn -DECORE_FILE="$ECORE_PATH" clean package
    exitCode=$?

    if [ "$exitCode" -ne 0 ]; then
        echo "Maven build failed once for $ECORE_PATH (exit code: $exitCode). Retry..."
        mvn -DECORE_FILE="$ECORE_PATH" clean package
        exitCode=$?

        if [ "$exitCode" -ne 0 ]; then
            echo "❌ Maven build failed for $ECORE_PATH (exit code: $exitCode). Skip..."
            touch "$OUTPUT_DIR/err.$(basename "$ECORE_PATH")"
            continue
        fi
    fi

# Find the jar file in the target directory
    JAR_FILE=$(find modules/model/target -type f -name '*.jar' | head -n 1)

    if [ -z "$JAR_FILE" ]; then
        echo "No JAR file found in target/ after mvn package."
        exit 1
    fi

    # Create a new jar name based on the ecore file
    ECORE_BASENAME=$(basename "$ECORE_PATH" .ecore)
    NEW_JAR_NAME="$ECORE_BASENAME.jar"

    # Copy and rename jar to output folder
    cp -f "$JAR_FILE" "$OUTPUT_DIR/$NEW_JAR_NAME"
    echo "Copied and renamed to $OUTPUT_DIR/$NEW_JAR_NAME"
done

echo "All done."