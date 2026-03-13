param(
    [switch]$NoRun
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Path $PSScriptRoot -Parent
$envFilePath = Join-Path $projectRoot '.env'

if (-not (Test-Path $envFilePath)) {
    throw "File .env not found at: $envFilePath"
}

function Import-DotEnv {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $loadedKeys = New-Object System.Collections.Generic.List[string]

    Get-Content -Path $Path | ForEach-Object {
        $line = $_.Trim()

        if ([string]::IsNullOrWhiteSpace($line)) { return }
        if ($line.StartsWith('#')) { return }

        $separatorIndex = $line.IndexOf('=')
        if ($separatorIndex -lt 1) { return }

        $key = $line.Substring(0, $separatorIndex).Trim()
        $value = $line.Substring($separatorIndex + 1).Trim()

        if ((($value.StartsWith('"')) -and ($value.EndsWith('"'))) -or (($value.StartsWith("'")) -and ($value.EndsWith("'")))) {
            $value = $value.Substring(1, $value.Length - 2)
        }

        [System.Environment]::SetEnvironmentVariable($key, $value, 'Process')
        $loadedKeys.Add($key)
    }

    return $loadedKeys
}

Push-Location $projectRoot
try {
    $keys = Import-DotEnv -Path $envFilePath

    [System.Environment]::SetEnvironmentVariable('SPRING_PROFILES_ACTIVE', 'local', 'Process')

    Write-Host "Loaded environment variables from .env: $($keys.Count)"
    Write-Host 'SPRING_PROFILES_ACTIVE=local'

    if ($NoRun) {
        Write-Host 'NoRun mode enabled. Application start skipped.'
        return
    }

    & .\gradlew.bat bootRun
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}
finally {
    Pop-Location
}
