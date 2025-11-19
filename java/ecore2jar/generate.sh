#!/bin/bash
set -e  # Exit on error

# Check if folder arguments are provided
if [[ $# -ne 2 ]]; then
    echo "Usage: $0 <source folder> <target folder>"
    exit 1
fi

INPUT_DIR="$1"

# Check if folder exists
if [[ ! -d "$INPUT_DIR" ]]; then
    echo "Error: Folder '$INPUT_DIR' does not exist."
    exit 1
fi

# Create output directory
OUTPUT_DIR="$2"
mkdir -p "$OUTPUT_DIR"

# CSV output path
CSV_REPORT="$OUTPUT_DIR/build_report.csv"

# If report doesn't exist, create with header
if [[ ! -f "$CSV_REPORT" ]]; then
    echo "FilePath,Status" > "$CSV_REPORT"
fi

# Get all .ecore files in the input folder
mapfile -t ECORE_FILES < <(find "$INPUT_DIR" -type f -name "*.ecore")

if [[ ${#ECORE_FILES[@]} -eq 0 ]]; then
    echo "No .ecore files found in $INPUT_DIR."
    exit 0
fi

for ECORE_PATH in "${ECORE_FILES[@]}"; do
    # Check if this file is already in the report
    if grep -Fq "\"$ECORE_PATH\"" "$CSV_REPORT"; then
        echo "⏩ Skipping $ECORE_PATH (already in build report)"
        continue
    fi

    echo "Processing $ECORE_PATH..."
    
    # Run Maven build (with environment variable)
    echo "Running Maven build..."
    if ! mvn -DECORE_FILE="$ECORE_PATH" clean package; then
        echo "Maven build failed once for $ECORE_PATH. Retrying..."
        if ! mvn -DECORE_FILE="$ECORE_PATH" clean package; then
            echo "❌ Maven build failed for $ECORE_PATH. Skipping..."
            echo "\"$ECORE_PATH\",Failed" >> "$CSV_REPORT"
            continue
        fi
    fi

    # Find main JAR (exclude -sources.jar and -javadoc.jar)
    jarFile=$(find modules/model/target -type f -name "*.jar" \
        ! -name "*-sources.jar" ! -name "*-javadoc.jar" | head -n 1)

    if [[ -z "$jarFile" ]]; then
        echo "No main JAR file found in target/ after mvn package."
        echo "\"$ECORE_PATH\",\"Failed - No JAR\"" >> "$CSV_REPORT"
        continue
    fi

    # Base name for renaming
    ECORE_BASENAME=$(basename "$ECORE_PATH" .ecore)

    # Copy and rename JAR
    newJarName="$ECORE_BASENAME.jar"
    cp -f "$jarFile" "$OUTPUT_DIR/$newJarName"
    echo "Copied and renamed to $OUTPUT_DIR/$newJarName"

    # Look for generated model-fixed.ecore
    fixedEcore="modules/model/model/gen/model-fixed.ecore"
    if [[ -f "$fixedEcore" ]]; then
        newEcoreName="$ECORE_BASENAME.ecore"
        cp -f "$fixedEcore" "$OUTPUT_DIR/$newEcoreName"
        echo "Copied model-fixed.ecore to $OUTPUT_DIR/$newEcoreName"
    else
        echo "⚠️ model-fixed.ecore not found for $ECORE_PATH"
    fi

    # Record success
    echo "\"$ECORE_PATH\",Success" >> "$CSV_REPORT"
done

echo "📄 Build report saved to $CSV_REPORT"
echo "All done."
