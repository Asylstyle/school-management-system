param([switch]$Test, [switch]$Run, [switch]$Demo)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force build/classes | Out-Null
    $sources = @(Get-ChildItem src/main/java,src/test/java -Recurse -Filter '*.java' | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' })
    [System.IO.File]::WriteAllLines((Join-Path $PSScriptRoot 'build/sources.txt'), $sources, (New-Object System.Text.UTF8Encoding($false)))
    javac --release 17 -encoding UTF-8 -d build/classes '@build/sources.txt'
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
    if ($Test) {
        java -cp build/classes school.AllTests
        if ($LASTEXITCODE -ne 0) { throw 'Tests failed' }
    }
    if ($Demo) { java -cp build/classes school.ConsoleApp --demo }
    elseif ($Run) { java -cp build/classes school.ConsoleApp }
    if ($LASTEXITCODE -ne 0) { throw 'Application failed' }
} finally { Pop-Location }
