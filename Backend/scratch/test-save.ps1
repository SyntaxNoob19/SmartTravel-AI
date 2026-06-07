$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
try {
    $register = Invoke-RestMethod -Uri "http://localhost:9090/api/auth/register" -Method Post -Body '{"name":"Demo User","email":"demo@smarttravel.com","password":"Demo@1234"}' -ContentType "application/json" -WebSession $session
    Write-Host "Register Response:" ($register | ConvertTo-Json -Depth 2)
} catch {
    Write-Host "Registration failed or user already exists, attempting login..."
    $login = Invoke-RestMethod -Uri "http://localhost:9090/api/auth/login" -Method Post -Body '{"email":"demo@smarttravel.com","password":"Demo@1234"}' -ContentType "application/json" -WebSession $session
    Write-Host "Login Response:" ($login | ConvertTo-Json -Depth 2)
}

$payload = @{
    userId = 1
    userEmail = "demo@smarttravel.com"
    tripName = "Jaipur Trip"
    destination = "Jaipur"
    plannerRequest = @{
        city = "Jaipur"
        days = 3
        travellerType = "solo"
    }
    plannerResponse = @{
        generatedDays = 3
        totalPlaces = 9
        travellerType = "solo"
        dataSource = "AI_GENERATED"
        summary = "Jaipur 3-day itinerary"
        itinerary = @(
            @{
                dayNumber = 1
                daySummary = "Explore Jaipur monuments"
                places = @(
                    @{
                        placeName = "Amber Fort"
                        plannedVisitTimeSlot = "Morning"
                        description = "amber fort visit"
                    }
                )
            }
        )
    }
}
$jsonPayload = $payload | ConvertTo-Json -Depth 10
try {
    $res = Invoke-RestMethod -Uri "http://localhost:9090/api/trips/users/demo@smarttravel.com" -Method Post -Body $jsonPayload -ContentType "application/json" -WebSession $session
    Write-Host "Save Response:" ($res | ConvertTo-Json -Depth 2)
} catch {
    Write-Error $_
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        Write-Host "Response Error:" $reader.ReadToEnd()
    }
}
