$files = Get-ChildItem -Path "D:\SWP_Project\Code\SWP391_Group1_ShoeOnlineShopSystem\ShoesOnlineShop\src\main\resources\templates\*.html" -Exclude "customer-*.html", "home.html", "cart.html", "login.html", "register.html", "layout.html"
foreach ($file in $files) {
    $content = Get-Content -Path $file.FullName -Raw
    $content = $content -replace 'href="/profile"', 'href="/internal/profile"'
    $content = $content -replace 'href="/profile/change-password"', 'href="/internal/profile/change-password"'
    $content = $content -replace 'action="/profile/change-password"', 'action="/internal/profile/change-password"'
    $content = $content -replace 'action="/profile/update"', 'action="/internal/profile/update"'
    $content = $content -replace 'th:action="@{/profile/update}"', 'th:action="@{/internal/profile/update}"'
    $content = $content -replace 'th:action="@{/profile/change-password}"', 'th:action="@{/internal/profile/change-password}"'
    Set-Content -Path $file.FullName -Value $content -Encoding UTF8
}
Write-Output "Done!"
