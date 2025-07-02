#! /bin/bash

# Exit on errors
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

# Loop over each .ecore file in the folder
for ECORE_PATH in "$INPUT_DIR"*.ecore; do
    if [ ! -e "$ECORE_PATH" ]; then
        echo "No .ecore files found in $INPUT_DIR."
        break
    fi

    echo "Processing $ECORE_PATH..."

    # Set environment variable
    export ECORE_FILE="$ECORE_PATH"

    # Run Maven package
    mvn -DECORE_FILE="$ECORE_PATH" clean package

    # Find the jar file in the target directory
    JAR_FILE=$(find modules/model/target -name "*.jar" | head -n 1)
    if [ -z "$JAR_FILE" ]; then
        echo "No JAR file found in target/ after mvn package."
        exit 1
    fi


    # Create a new jar name based on the ecore file
    ECORE_BASENAME=$(basename "$ECORE_PATH" .ecore)
    NEW_JAR_NAME="${ECORE_BASENAME}.jar"

    # Copy and rename jar to output folder
    cp "$JAR_FILE" "$OUTPUT_DIR/$NEW_JAR_NAME"
    echo "Copied and renamed to $OUTPUT_DIR/$NEW_JAR_NAME"
done

echo "All done."
