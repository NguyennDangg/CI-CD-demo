$ErrorActionPreference = 'Stop'

$demoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$topicCollectionRoot = Split-Path -Parent $demoRoot

function Get-JavaMajorVersion([string]$javaExe) {
    $previousPreference = $ErrorActionPreference
    try {
        $ErrorActionPreference = 'Continue'
        $versionOutput = (& $javaExe -version 2>&1 | Out-String)
    }
    finally {
        $ErrorActionPreference = $previousPreference
    }
    if ($versionOutput -match 'version "1\.(\d+)') {
        return [int]$Matches[1]
    }
    if ($versionOutput -match 'version "(\d+)') {
        return [int]$Matches[1]
    }
    return 0
}

function Get-JavaVersionText([string]$javaExe) {
    $previousPreference = $ErrorActionPreference
    try {
        $ErrorActionPreference = 'Continue'
        $versionOutput = (& $javaExe -version 2>&1 | Out-String)
    }
    finally {
        $ErrorActionPreference = $previousPreference
    }

    $versionLine = $versionOutput -split '\r?\n' |
        Where-Object { $_ -match 'version "' } |
        Select-Object -First 1
    return ($versionLine -replace '^java\.exe\s*:\s*', '').Trim()
}

$javaCandidates = @()
if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
    $javaCandidates += Join-Path $env:JAVA_HOME 'bin\java.exe'
}
$pathJava = Get-Command java.exe -ErrorAction SilentlyContinue
if ($pathJava) {
    $javaCandidates += $pathJava.Source
}
$collectionJdk = Join-Path $topicCollectionRoot 'visual_demo_java\oracleJdk-27\bin\java.exe'
if (Test-Path $collectionJdk) {
    $javaCandidates += $collectionJdk
}

$javaExe = $null
foreach ($candidate in ($javaCandidates | Select-Object -Unique)) {
    if ((Get-JavaMajorVersion $candidate) -ge 17) {
        $javaExe = (Resolve-Path $candidate).Path
        break
    }
}
if (-not $javaExe) {
    throw 'Java 17 or newer is required. Install a JDK or use the JDK bundled with visual_demo_java in the topic collection.'
}

$javaHome = Split-Path -Parent (Split-Path -Parent $javaExe)
$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;$env:PATH"

$mavenCommand = Get-Command mvn.cmd -ErrorAction SilentlyContinue
$mavenExe = if ($mavenCommand) { $mavenCommand.Source } else { $null }
if (-not $mavenExe) {
    $mavenCache = Join-Path $env:USERPROFILE '.m2\wrapper\dists'
    if (Test-Path $mavenCache) {
        $mavenExe = Get-ChildItem $mavenCache -Filter mvn.cmd -File -Recurse |
            Where-Object { $_.FullName -match '\\bin\\mvn\.cmd$' } |
            Sort-Object FullName -Descending |
            Select-Object -First 1 -ExpandProperty FullName
    }
}
if (-not $mavenExe) {
    throw 'Maven was not found. Install Maven 3.8 or newer, then run this script again.'
}

$javaVersionText = Get-JavaVersionText $javaExe
Write-Host "Using Java: $javaVersionText"
Write-Host 'Building and running tests...'
Push-Location $demoRoot
try {
    & $mavenExe --batch-mode --no-transfer-progress clean verify
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }

    Write-Host 'Running the Java demo...'
    & $javaExe -cp target/classes demo.OrderCalculator
}
finally {
    Pop-Location
}
