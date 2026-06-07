@echo off
REM SmartTravel External APIs Test Script
REM This script tests all the newly integrated external APIs
REM Run this after: mvn clean install && mvn spring-boot:run

setlocal enabledelayedexpansion

echo.
echo ====================================================
echo SmartTravel External APIs - Test Suite
echo ====================================================
echo.

set BASE_URL=http://localhost:9090/api/v1/external

REM Color codes for output
for /F %%A in ('copy /Z "%~f0" nul') do set "BS=%%A"

echo [TEST 1] GET CURRENT WEATHER
echo Testing: GET %BASE_URL%/weather/current
curl -X GET "%BASE_URL%/weather/current?latitude=28.6139^&longitude=77.2090"
echo.
echo.

echo [TEST 2] GET WEATHER FORECAST
echo Testing: GET %BASE_URL%/weather/forecast
curl -X GET "%BASE_URL%/weather/forecast?latitude=28.6139^&longitude=77.2090"
echo.
echo.

echo [TEST 3] GET WEATHER FOR A PLACE
echo Testing: GET %BASE_URL%/weather/place/Delhi
curl -X GET "%BASE_URL%/weather/place/Delhi"
echo.
echo.

echo [TEST 4] GET HOLIDAYS
echo Testing: GET %BASE_URL%/holidays/2024/IN
curl -X GET "%BASE_URL%/holidays/2024/IN"
echo.
echo.

echo [TEST 5] GET LONG WEEKENDS
echo Testing: GET %BASE_URL%/holidays/long-weekends/2024/3/IN
curl -X GET "%BASE_URL%/holidays/long-weekends/2024/3/IN"
echo.
echo.

echo [TEST 6] GET UPCOMING HOLIDAYS
echo Testing: GET %BASE_URL%/holidays/upcoming
curl -X GET "%BASE_URL%/holidays/upcoming"
echo.
echo.

echo [TEST 7] FORWARD GEOCODING
echo Testing: GET %BASE_URL%/geocode/forward
curl -X GET "%BASE_URL%/geocode/forward?address=Taj%%20Mahal,%%20Agra"
echo.
echo.

echo [TEST 8] REVERSE GEOCODING
echo Testing: GET %BASE_URL%/geocode/reverse
curl -X GET "%BASE_URL%/geocode/reverse?latitude=27.1751^&longitude=78.0421"
echo.
echo.

echo [TEST 9] SEARCH PLACES
echo Testing: GET %BASE_URL%/geocode/search
curl -X GET "%BASE_URL%/geocode/search?query=temples%%20in%%20Varanasi"
echo.
echo.

echo [TEST 10] SEARCH IMAGES
echo Testing: GET %BASE_URL%/images/search
echo Note: Requires UNSPLASH_API_KEY environment variable
curl -X GET "%BASE_URL%/images/search?query=Taj%%20Mahal"
echo.
echo.

echo [TEST 11] GET IMAGES BY CITY
echo Testing: GET %BASE_URL%/images/city/Delhi
curl -X GET "%BASE_URL%/images/city/Delhi"
echo.
echo.

echo [TEST 12] GET CROWD ALERTS
echo Testing: GET %BASE_URL%/alerts/crowd/Delhi
curl -X GET "%BASE_URL%/alerts/crowd/Delhi"
echo.
echo.

echo [TEST 13] GET DANGER ZONE ALERTS
echo Testing: GET %BASE_URL%/alerts/danger/Mumbai
curl -X GET "%BASE_URL%/alerts/danger/Mumbai"
echo.
echo.

echo [TEST 14] GET CRITICAL ALERTS
echo Testing: GET %BASE_URL%/alerts/critical
curl -X GET "%BASE_URL%/alerts/critical"
echo.
echo.

echo [TEST 15] GET ALERTS IN RADIUS
echo Testing: GET %BASE_URL%/alerts/radius
curl -X GET "%BASE_URL%/alerts/radius?latitude=28.6139^&longitude=77.2090^&radiusKm=10"
echo.
echo.

echo [TEST 16] GET COMPREHENSIVE PLACE INFO
echo Testing: GET %BASE_URL%/place-info/Agra
curl -X GET "%BASE_URL%/place-info/Agra"
echo.
echo.

echo [TEST 17] GET TRIP PLANNER INFO
echo Testing: GET %BASE_URL%/trip-planner/Delhi/IN
curl -X GET "%BASE_URL%/trip-planner/Delhi/IN"
echo.
echo.

echo ====================================================
echo Test Suite Completed!
echo ====================================================
echo.
echo Summary:
echo - Total Tests: 17
echo - If you see JSON responses, the APIs are working!
echo - Check QUICK_START.md for detailed documentation
echo - Check API_INTEGRATIONS.md for full API reference
echo.
pause
