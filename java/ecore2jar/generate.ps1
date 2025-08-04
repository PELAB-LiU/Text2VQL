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

# CSV output path
$CSV_REPORT = "$OUTPUT_DIR/build_report.csv"
$results = @()

# Get all .ecore files in the input folder
$ecoreFiles = Get-ChildItem -Path $INPUT_DIR -Filter *.ecore -Recurse -File

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

    # Run Maven package and capture the result
    Write-Host "Running Maven build..."
    & mvn -DECORE_FILE="$ECORE_PATH" clean package
    $exitCode = $LASTEXITCODE

    if ($exitCode -ne 0) {
        Write-Host "Maven build failed once for $ECORE_PATH (exit code: $exitCode). Retry..."
        & mvn -DECORE_FILE="$ECORE_PATH" clean package
        $exitCode = $LASTEXITCODE
    }

    if ($exitCode -ne 0) {
        Write-Host "❌ Maven build failed for $ECORE_PATH (exit code: $exitCode). Skipping..."
        $results += [PSCustomObject]@{
            FilePath = $ECORE_PATH
            Status   = "Failed"
        }
        continue
    }

    # Find the main JAR (exclude -sources.jar and -javadoc.jar)
    $jarFile = Get-ChildItem -Path "modules/model/target" -Filter *.jar -Recurse |
               Where-Object { $_.Name -notmatch "-sources\.jar$" -and $_.Name -notmatch "-javadoc\.jar$" } |
               Select-Object -First 1

    # Find the sources JAR
    $sourcesJar = Get-ChildItem -Path "modules/model/target" -Filter *-sources.jar -Recurse |
                  Select-Object -First 1

    if (-not $jarFile) {
        Write-Host "No main JAR file found in target/ after mvn package."
        $results += [PSCustomObject]@{
            FilePath = $ECORE_PATH
            Status   = "Failed - No JAR"
        }
        continue
    }

    # Base name for renaming
    $ECORE_BASENAME = [System.IO.Path]::GetFileNameWithoutExtension($ECORE_PATH)

    # Copy main JAR
    $newJarName = "$ECORE_BASENAME.jar"
    Copy-Item -Path $jarFile.FullName -Destination (Join-Path $OUTPUT_DIR $newJarName) -Force
    Write-Host "Copied and renamed to $OUTPUT_DIR\$newJarName"

    # Copy sources JAR if it exists
    if ($sourcesJar) {
        $newSourcesName = "$ECORE_BASENAME-sources.jar"
        Copy-Item -Path $sourcesJar.FullName -Destination (Join-Path $OUTPUT_DIR $newSourcesName) -Force
        Write-Host "Copied sources JAR to $OUTPUT_DIR\$newSourcesName"
    } else {
        Write-Host "⚠ No sources JAR found for $ECORE_PATH."
    }

    # Record success
    $results += [PSCustomObject]@{
        FilePath = $ECORE_PATH
        Status   = "Success"
    }
}

# Save results to CSV
$results | Export-Csv -Path $CSV_REPORT -NoTypeInformation
Write-Host "📄 Build report saved to $CSV_REPORT"
Write-Host "All done."
