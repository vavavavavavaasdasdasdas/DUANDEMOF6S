$html = Invoke-WebRequest -Uri http://localhost:8080/ -UseBasicParsing
$links = ($html.Links | Select-Object -ExpandProperty href) | Where-Object { $_ -like '/*' } | Sort-Object -Unique

Write-Host "Links found on homepage:"
foreach ($link in $links) {
    try {
        $res = Invoke-WebRequest -Uri "http://localhost:8080$link" -UseBasicParsing -ErrorAction Stop
        Write-Host "$link -> $($res.StatusCode)"
    } catch {
        if ($_.Exception.Response) {
            Write-Host "$link -> $($_.Exception.Response.StatusCode.value__)"
        } else {
            Write-Host "$link -> ERROR $($_.Exception.Message)"
        }
    }
}
