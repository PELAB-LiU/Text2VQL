# Exit on error
$ErrorActionPreference = "Stop"

# Check if folder arguments are provided
if ($args.Count -ne 2) {
    Write-Host "Usage: $PSCommandPath <source folder> <target folder>"
    exit 1
}

$INPUT_DIR = $args[0]

# Check if folder exists
if (-not (Test-Path -Path $INPUT_DIR -PathType Container)) {
    Write-Host "Error: Folder '$INPUT_DIR' does not exist."
    exit 1
}

# Create output directory
$OUTPUT_DIR = $args[1]
New-Item -ItemType Directory -Path $OUTPUT_DIR -Force | Out-Null

# CSV output path
$CSV_REPORT = Join-Path $OUTPUT_DIR "build_report.csv"

# If CSV doesn't exist, create with header
if (-not (Test-Path $CSV_REPORT)) {
    "FilePath,Status" | Out-File -FilePath $CSV_REPORT -Encoding utf8
}

# Load existing report
$existingResults = Import-Csv -Path $CSV_REPORT

# Get all .ecore files in the input folder
$ecoreFiles = Get-ChildItem -Path $INPUT_DIR -Filter *.ecore -Recurse -File

if ($ecoreFiles.Count -eq 0) {
    Write-Host "No .ecore files found in $INPUT_DIR."
    exit 0
}

foreach ($ecoreFile in $ecoreFiles) {
    $ECORE_PATH = $ecoreFile.FullName

    # Skip if already in report
    if ($existingResults | Where-Object { $_.FilePath -eq $ECORE_PATH }) {
        Write-Host "⏩ Skipping $ECORE_PATH (already in build report)"
        continue
    }

    Write-Host "Processing $ECORE_PATH..."

    # Copy ECORE file to modules/model/model/model.ecore
    $destinationPath = "modules/model/model/model.ecore"
    New-Item -ItemType Directory -Path (Split-Path $destinationPath) -Force | Out-Null
    Copy-Item -Path $ECORE_PATH -Destination $destinationPath -Force
    Write-Host "Copied $ECORE_PATH to $destinationPath"

    # Set environment variable
    $env:ECORE_FILE = $ECORE_PATH

    # Run Maven build
    Write-Host "Running Maven build..."
    & mvn -DECORE_FILE="$ECORE_PATH" clean package
    $exitCode = $LASTEXITCODE

    if ($exitCode -ne 0) {
        Write-Host "Maven build failed once for $ECORE_PATH (exit code: $exitCode). Retrying..."
        & mvn -DECORE_FILE="$ECORE_PATH" clean package
        $exitCode = $LASTEXITCODE
    }

    if ($exitCode -ne 0) {
        Write-Host "❌ Maven build failed for $ECORE_PATH (exit code: $exitCode). Skipping..."
        Add-Content -Path $CSV_REPORT -Value "`"$ECORE_PATH`",Failed"
        $existingResults += [PSCustomObject]@{ FilePath = $ECORE_PATH; Status = "Failed" }
        continue
    }

    # Find main JAR (exclude -sources.jar and -javadoc.jar)
    $jarFile = Get-ChildItem -Path "modules/model/target" -Filter *.jar -Recurse |
               Where-Object { $_.Name -notmatch "-sources\.jar$" -and $_.Name -notmatch "-javadoc\.jar$" } |
               Select-Object -First 1

    if (-not $jarFile) {
        Write-Host "No main JAR file found in target/ after mvn package."
        Add-Content -Path $CSV_REPORT -Value "`"$ECORE_PATH`","Failed - No JAR""
        $existingResults += [PSCustomObject]@{ FilePath = $ECORE_PATH; Status = "Failed - No JAR" }
        continue
    }

    # Rename and copy JAR
    $ECORE_BASENAME = [System.IO.Path]::GetFileNameWithoutExtension($ECORE_PATH)
    $newJarName = "$ECORE_BASENAME.jar"
    Copy-Item -Path $jarFile.FullName -Destination (Join-Path $OUTPUT_DIR $newJarName) -Force
    Write-Host "Copied and renamed to $OUTPUT_DIR\$newJarName"

    # Record success immediately
    Add-Content -Path $CSV_REPORT -Value "`"$ECORE_PATH`",Success"
    $existingResults += [PSCustomObject]@{ FilePath = $ECORE_PATH; Status = "Success" }
}

Write-Host "📄 Build report saved to $CSV_REPORT"
Write-Host "All done."
