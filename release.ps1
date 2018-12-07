if (Test-Path -Path build) {
    Remove-Item -Path build -Recurse
}
.\gradlew.bat build

$outFolder = 'es-release.prebuilt'
if (Test-Path -Path $outFolder) {
    Remove-Item -Path $outFolder -Recurse
}
mkdir $outFolder
Copy-Item -Path .\build\libs\ebook-server-*-RELEASE.war -Destination $outFolder

Push-Location -Path ui
Remove-Item -Path build -Recurse
npm run build
Pop-Location
Copy-Item -Path .\ui\build -Destination $outFolder\ui -Recurse

if (-not (Get-Command Compress-7Zip -ErrorAction Ignore)) {
    Install-Package -Scope CurrentUser 7Zip4PowerShell
}

$outTarPath = "$outFolder.tar"
if (Test-Path -Path $outTarPath) {
    Remove-Item $outTarPath
}

$outTarGzipPath = "$outTarPath.gz"
if (Test-Path -Path $outTarGzipPath) {
    Remove-Item $outTarGzipPath
}

Compress-7Zip -ArchiveFileName $outTarPath -Path $outFolder -Format Tar
Compress-7Zip -ArchiveFileName $outTarGzipPath -Path $outTarPath -Format GZip

Remove-Item $outTarPath
Remove-Item $outFolder -Recurse
