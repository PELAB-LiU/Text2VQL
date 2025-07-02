# Exit on error
$ErrorActionPreference = "Stop"

# Check if folder argument is provided
if ($args.Count -ne 1) {
    Write-Host "Usage: $PSCommandPath <folder>"
    exit 1
}

$INPUT_DIR = $args[0]

# Check if folder exists
if (-not (Test-Path -Path $INPUT_DIR -PathType Container)) {
    Write-Host "Error: Folder '$INPUT_DIR' does not exist."
    exit 1
}

# Create a directory for collected jars
$OUTPUT_DIR = "domains"
New-Item -ItemType Directory -Path $OUTPUT_DIR -Force | Out-Null

# Get all .ecore files in the input folder
$ecoreFiles = Get-ChildItem -Path $INPUT_DIR -Filter *.ecore

if ($ecoreFiles.Count -eq 0) {
    Write-Host "No .ecore files found in $INPUT_DIR."
    exit 0
}

foreach ($ecoreFile in $ecoreFiles) {
    $ECORE_PATH = $ecoreFile.FullName
    Write-Host "Processing $ECORE_PATH..."

    # Copy ECORE file to modules/model/model/model.ecore
    $destinationPath = "modules/model/model/model.ecore"
    Copy-Item -Path $ECORE_PATH -Destination $destinationPath -Force
    Write-Host "Copied $ECORE_PATH to $destinationPath"

    # Set environment variable
    $env:ECORE_FILE = $ECORE_PATH

    # Run Maven package
    mvn -DECORE_FILE="$ECORE_PATH" clean package

    # Find the jar file in the target directory
    $jarFiles = Get-ChildItem -Path "modules/model/target" -Filter *.jar -Recurse | Select-Object -First 1

    if (-not $jarFiles) {
        Write-Host "No JAR file found in target/ after mvn package."
        exit 1
    }

    $JAR_FILE = $jarFiles.FullName

    # Create a new jar name based on the ecore file
    $ECORE_BASENAME = [System.IO.Path]::GetFileNameWithoutExtension($ECORE_PATH)
    $NEW_JAR_NAME = "$ECORE_BASENAME.jar"

    # Copy and rename jar to output folder
    Copy-Item -Path $JAR_FILE -Destination (Join-Path $OUTPUT_DIR $NEW_JAR_NAME) -Force
    Write-Host "Copied and renamed to $OUTPUT_DIR\$NEW_JAR_NAME"
}

Write-Host "All done."
