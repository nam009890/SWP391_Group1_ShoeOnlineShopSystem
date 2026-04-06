$files = Get-ChildItem -Path "src\main\resources\templates\admin\*.html"
foreach ($file in $files) {
    $content = Get-Content -Raw -Path $file.FullName
    $content = $content -replace '#request\.requestURI', 'requestURI'
    Set-Content -Path $file.FullName -Value $content -NoNewline
    Write-Host "Fixed" $file.Name
}
Write-Host "Done - replaced #request.requestURI with requestURI in all admin templates."
