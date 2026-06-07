# SmartTravel External APIs Test Script (PowerShell)
# This script tests all the newly integrated external APIs
# Run this after: mvn clean install && mvn spring-boot:run

$BaseURL = "https://smarttravel-ai.onrender.com/api/v1/external"

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "SmartTravel External APIs - Test Suite" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

$testNumber = 1

function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Endpoint
    )
    
    Write-Host "[$testNumber] $Name" -ForegroundColor Yellow
    Write-Host "Testing: GET $Endpoint" -ForegroundColor Gray
    
    try {
        $response = Invoke-WebRequest -Uri $Endpoint -Method Get -ErrorAction Stop
        Write-Host "✓ Success (Status: $($response.StatusCode))" -ForegroundColor Green
        Write-Host ($response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 2) -ForegroundColor White
    } catch {
        Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host ""
    $script:testNumber++
}

# Weather Endpoints
Test-Endpoint "GET CURRENT WEATHER" "$BaseURL/weather/current?latitude=28.6139&longitude=77.2090"
Test-Endpoint "GET WEATHER FORECAST" "$BaseURL/weather/forecast?latitude=28.6139&longitude=77.2090"
Test-Endpoint "GET WEATHER FOR A PLACE" "$BaseURL/weather/place/Delhi"

# Holiday Endpoints
Test-Endpoint "GET HOLIDAYS" "$BaseURL/holidays/2024/IN"
Test-Endpoint "GET LONG WEEKENDS" "$BaseURL/holidays/long-weekends/2024/3/IN"
Test-Endpoint "GET UPCOMING HOLIDAYS" "$BaseURL/holidays/upcoming"
Test-Endpoint "GET CURRENT YEAR HOLIDAYS" "$BaseURL/holidays/current/IN"

# Geocoding Endpoints
Test-Endpoint "FORWARD GEOCODING" "$BaseURL/geocode/forward?address=Taj%20Mahal,%20Agra"
Test-Endpoint "REVERSE GEOCODING" "$BaseURL/geocode/reverse?latitude=27.1751&longitude=78.0421"
Test-Endpoint "SEARCH PLACES" "$BaseURL/geocode/search?query=temples%20in%20Varanasi"

# Image Endpoints
Test-Endpoint "SEARCH IMAGES" "$BaseURL/images/search?query=Taj%20Mahal"
Test-Endpoint "GET IMAGES BY CITY" "$BaseURL/images/city/Delhi"
Test-Endpoint "GET IMAGES BY PLACE" "$BaseURL/images/place/India%20Gate"
Test-Endpoint "GET RANDOM IMAGES" "$BaseURL/images/random?query=adventure%20travel"

# Alert Endpoints
Test-Endpoint "GET CROWD ALERTS" "$BaseURL/alerts/crowd/Delhi"
Test-Endpoint "GET DANGER ZONE ALERTS" "$BaseURL/alerts/danger/Mumbai"
Test-Endpoint "GET CRITICAL ALERTS" "$BaseURL/alerts/critical"
Test-Endpoint "CHECK PLACE DANGER ZONE" "$BaseURL/alerts/danger/check/place_123"
Test-Endpoint "GET ALERTS IN RADIUS" "$BaseURL/alerts/radius?latitude=28.6139&longitude=77.2090&radiusKm=10"

# Combined Endpoints
Test-Endpoint "GET COMPREHENSIVE PLACE INFO" "$BaseURL/place-info/Agra"
Test-Endpoint "GET TRIP PLANNER INFO" "$BaseURL/trip-planner/Delhi/IN"

Write-Host "=====================================================  " -ForegroundColor Cyan
Write-Host "Test Suite Completed!" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Summary:" -ForegroundColor Yellow
Write-Host "- Total Tests: $($testNumber - 1)" -ForegroundColor White
Write-Host "- If you see JSON responses, the APIs are working!" -ForegroundColor Green
Write-Host "- Check QUICK_START.md for detailed documentation" -ForegroundColor White
Write-Host "- Check API_INTEGRATIONS.md for full API reference" -ForegroundColor White
Write-Host ""
