# Exit on error
$ErrorActionPreference = "Stop"

# Check if folder arguments are provided
param(
    [Parameter(Mandatory=$true)][string]$InputDir,
    [Parameter(Mandatory=$true)][string]$OutputDir
)

if (-not (Test-Path $InputDir -PathType Container)) {
    Write-Host "Error: Folder '$InputDir' does not exist."
    exit 1
}

# Create output directory if it doesn't exist
if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
}

# CSV output path
$CsvReport = Join-Path $OutputDir "build_report.csv"

# If report doesn't exist, create with header
if (-not (Test-Path $CsvReport)) {
    "FilePath,Status" | Out-File -FilePath $CsvReport -Encoding UTF8
}

# Get all .ecore files recursively
$EcoreFiles = Get-ChildItem -Path $InputDir -Filter "*.ecore" -Recurse

if ($EcoreFiles.Count -eq 0) {
    Write-Host "No .ecore files found in $InputDir."
    exit 0
}

foreach ($EcoreFile in $EcoreFiles) {
    $EcorePath = $EcoreFile.FullName

    # Check if this file is already in the report
    if (Select-String -Path $CsvReport -Pattern [regex]::Escape($EcorePath)) {
        Write-Host "⏩ Skipping $EcorePath (already in build report)"
        continue
    }

    Write-Host "Processing $EcorePath..."
    Write-Host "Running Maven build..."

    $buildSuccess = $false

    # Try Maven build up to 2 times
    for ($i=0; $i -lt 2; $i++) {
        try {
            & mvn "-DECORE_FILE=$EcorePath" clean package
            $buildSuccess = $true
            break
        } catch {
            Write-Host "Maven build failed attempt $($i+1) for $EcorePath."
        }
    }

    if (-not $buildSuccess) {
        Write-Host "❌ Maven build failed for $EcorePath. Skipping..."
        "$EcorePath,Failed" | Out-File -FilePath $CsvReport -Append -Encoding UTF8
        continue
    }

    # Find main JAR (exclude -sources.jar and -javadoc.jar)
    $JarFile = Get-ChildItem -Path "modules/model/target" -Filter "*.jar" -Recurse |
               Where-Object { $_.Name -notmatch "(-sources|-javadoc)\.jar$" } |
               Select-Object -First 1

    if (-not $JarFile) {
        Write-Host "No main JAR file found in target/ after mvn package."
        "$EcorePath,Failed - No JAR" | Out-File -FilePath $CsvReport -Append -Encoding UTF8
        continue
    }

    # Base name for renaming
    $EcoreBaseName = [System.IO.Path]::GetFileNameWithoutExtension($EcorePath)

    # Copy and rename JAR
    $NewJarName = "$EcoreBaseName.jar"
    Copy-Item -Path $JarFile.FullName -Destination (Join-Path $OutputDir $NewJarName) -Force
    Write-Host "Copied and renamed to $(Join-Path $OutputDir $NewJarName)"

    # Record success
    "$EcorePath,Success" | Out-File -FilePath $CsvReport -Append -Encoding UTF8
}

Write-Host "📄 Build report saved to $CsvReport"
Write-Host "All done."
