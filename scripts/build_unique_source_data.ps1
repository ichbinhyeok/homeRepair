param(
    [string]$MetroMasterPath = "src/main/resources/data/2026_US_Metro_Master_Data.json",
    [string]$OutputJsonPath = "src/main/resources/data/external/metro_unique_signals_2026.json",
    [string]$OutputCsvPath = "src/main/resources/static/data/metro_unique_signals_2026.csv",
    [string]$OutputStaticJsonPath = "src/main/resources/static/data/metro_unique_signals_2026.json"
)

$ErrorActionPreference = "Stop"

function Normalize-Score {
    param(
        [double]$Value,
        [double]$Min,
        [double]$Max
    )
    if ($Max -le $Min) {
        return 50.0
    }
    return (($Value - $Min) / ($Max - $Min)) * 100.0
}

function Get-StateCodeMap {
    $map = @{}
    $map["01"] = "AL"; $map["02"] = "AK"; $map["04"] = "AZ"; $map["05"] = "AR"; $map["06"] = "CA"
    $map["08"] = "CO"; $map["09"] = "CT"; $map["10"] = "DE"; $map["11"] = "DC"; $map["12"] = "FL"
    $map["13"] = "GA"; $map["15"] = "HI"; $map["16"] = "ID"; $map["17"] = "IL"; $map["18"] = "IN"
    $map["19"] = "IA"; $map["20"] = "KS"; $map["21"] = "KY"; $map["22"] = "LA"; $map["23"] = "ME"
    $map["24"] = "MD"; $map["25"] = "MA"; $map["26"] = "MI"; $map["27"] = "MN"; $map["28"] = "MS"
    $map["29"] = "MO"; $map["30"] = "MT"; $map["31"] = "NE"; $map["32"] = "NV"; $map["33"] = "NH"
    $map["34"] = "NJ"; $map["35"] = "NM"; $map["36"] = "NY"; $map["37"] = "NC"; $map["38"] = "ND"
    $map["39"] = "OH"; $map["40"] = "OK"; $map["41"] = "OR"; $map["42"] = "PA"; $map["44"] = "RI"
    $map["45"] = "SC"; $map["46"] = "SD"; $map["47"] = "TN"; $map["48"] = "TX"; $map["49"] = "UT"
    $map["50"] = "VT"; $map["51"] = "VA"; $map["53"] = "WA"; $map["54"] = "WV"; $map["55"] = "WI"
    $map["56"] = "WY"
    return $map
}

function To-TitleWords {
    param([string]$Value)
    if ([string]::IsNullOrWhiteSpace($Value)) {
        return ""
    }
    $textInfo = (Get-Culture).TextInfo
    $parts = @()
    foreach ($token in ($Value -split "\s+")) {
        if ([string]::IsNullOrWhiteSpace($token)) {
            continue
        }
        if ($token.Length -le 2) {
            $parts += $token.ToUpperInvariant()
        } else {
            $parts += $textInfo.ToTitleCase($token.ToLowerInvariant())
        }
    }
    return ($parts -join " ")
}

$metroMaster = Get-Content -Path $MetroMasterPath -Raw | ConvertFrom-Json

$metroRows = @()
foreach ($entry in $metroMaster.data.PSObject.Properties) {
    $metroCode = $entry.Name
    $value = $entry.Value
    $stateCode = ($metroCode -split "_")[-1]
    $metroRows += [PSCustomObject]@{
        metro_code = $metroCode
        metro_name = To-TitleWords((($metroCode -split "_")[0..(($metroCode -split "_").Length - 2)] -join " "))
        state_code = $stateCode
        labor_mult = [double]$value.labor_mult
        climate_zone = [string]$value.climate_zone
        regional_risk = [string]$value.risk
    }
}

$censusOwnerUrl = "https://api.census.gov/data/2024/acs/acs5?get=NAME,B25003_001E,B25003_002E&for=state:*"
$censusYearUrl = "https://api.census.gov/data/2024/acs/acs5?get=NAME,B25035_001E&for=state:*"
$femaUrl = "https://www.fema.gov/api/open/v2/DisasterDeclarationsSummaries?%24select=state,disasterNumber,declarationType,declarationDate&%24filter=declarationDate%20ge%20%272016-01-01T00:00:00.000z%27%20and%20declarationType%20eq%20%27DR%27&%24top=20000"

$stateCodeByFips = Get-StateCodeMap

$ownerByState = @{}
$ownerResp = Invoke-RestMethod -Uri $censusOwnerUrl -Method Get
for ($i = 1; $i -lt $ownerResp.Count; $i++) {
    $row = $ownerResp[$i]
    $fips = [string]$row[3]
    $state = $stateCodeByFips[$fips]
    if (-not $state) {
        continue
    }
    $total = [double]$row[1]
    $owner = [double]$row[2]
    $ownerPct = if ($total -gt 0) { [math]::Round(($owner / $total) * 100.0, 2) } else { 0.0 }
    $ownerByState[$state] = $ownerPct
}

$medianYearByState = @{}
$yearResp = Invoke-RestMethod -Uri $censusYearUrl -Method Get
for ($i = 1; $i -lt $yearResp.Count; $i++) {
    $row = $yearResp[$i]
    $fips = [string]$row[2]
    $state = $stateCodeByFips[$fips]
    if (-not $state) {
        continue
    }
    $medianYear = [int]$row[1]
    $medianYearByState[$state] = $medianYear
}

$femaByState = @{}
$femaResp = Invoke-RestMethod -Uri $femaUrl -Method Get
$grouped = $femaResp.DisasterDeclarationsSummaries | Group-Object state
foreach ($g in $grouped) {
    $uniqueDisasterCount = ($g.Group | Select-Object -ExpandProperty disasterNumber -Unique).Count
    $femaByState[$g.Name] = [int]$uniqueDisasterCount
}

$defaultOwner = 65.0
if ($ownerByState.Count -gt 0) {
    $defaultOwner = ($ownerByState.Values | Measure-Object -Average).Average
}
$defaultMedianYear = 1988
if ($medianYearByState.Count -gt 0) {
    $defaultMedianYear = [int][math]::Round(($medianYearByState.Values | Measure-Object -Average).Average)
}
$defaultFema = 10
if ($femaByState.Count -gt 0) {
    $defaultFema = [int][math]::Round(($femaByState.Values | Measure-Object -Average).Average)
}

$enriched = @()
foreach ($row in $metroRows) {
    $state = $row.state_code
    $ownerPct = if ($ownerByState.ContainsKey($state)) { [double]$ownerByState[$state] } else { [double]$defaultOwner }
    $medianYear = if ($medianYearByState.ContainsKey($state)) { [int]$medianYearByState[$state] } else { [int]$defaultMedianYear }
    $disasterCount = if ($femaByState.ContainsKey($state)) { [int]$femaByState[$state] } else { [int]$defaultFema }
    $legacyAge = [int][math]::Max(0, 2026 - $medianYear)

    $enriched += [PSCustomObject]@{
        metro_code = $row.metro_code
        metro_name = $row.metro_name
        state_code = $state
        labor_mult = [double]$row.labor_mult
        climate_zone = $row.climate_zone
        regional_risk = $row.regional_risk
        fema_major_disaster_10y = $disasterCount
        owner_occupancy_rate_pct = $ownerPct
        median_year_built = $medianYear
        legacy_housing_age_years = $legacyAge
    }
}

$laborValues = $enriched | Select-Object -ExpandProperty labor_mult
$disasterValues = $enriched | Select-Object -ExpandProperty fema_major_disaster_10y
$legacyValues = $enriched | Select-Object -ExpandProperty legacy_housing_age_years

$laborMin = ($laborValues | Measure-Object -Minimum).Minimum
$laborMax = ($laborValues | Measure-Object -Maximum).Maximum
$disasterMin = ($disasterValues | Measure-Object -Minimum).Minimum
$disasterMax = ($disasterValues | Measure-Object -Maximum).Maximum
$legacyMin = ($legacyValues | Measure-Object -Minimum).Minimum
$legacyMax = ($legacyValues | Measure-Object -Maximum).Maximum

$generatedAt = (Get-Date).ToString("yyyy-MM-dd")

$finalRows = @()
foreach ($row in $enriched) {
    $laborScore = Normalize-Score -Value ([double]$row.labor_mult) -Min $laborMin -Max $laborMax
    $disasterScore = Normalize-Score -Value ([double]$row.fema_major_disaster_10y) -Min $disasterMin -Max $disasterMax
    $legacyScore = Normalize-Score -Value ([double]$row.legacy_housing_age_years) -Min $legacyMin -Max $legacyMax
    $repairPressure = [math]::Round((0.45 * $laborScore) + (0.35 * $disasterScore) + (0.20 * $legacyScore), 1)

    $finalRows += [PSCustomObject]@{
        metro_code = $row.metro_code
        metro_name = $row.metro_name
        msa_name = "$($row.metro_name), $($row.state_code) MSA"
        state_code = $row.state_code
        labor_mult = [math]::Round([double]$row.labor_mult, 3)
        climate_zone = $row.climate_zone
        regional_risk = $row.regional_risk
        fema_major_disaster_10y = [int]$row.fema_major_disaster_10y
        owner_occupancy_rate_pct = [math]::Round([double]$row.owner_occupancy_rate_pct, 2)
        median_year_built = [int]$row.median_year_built
        legacy_housing_age_years = [int]$row.legacy_housing_age_years
        repair_pressure_index = $repairPressure
        source_fema = $femaUrl
        source_census_owner_occupancy = $censusOwnerUrl
        source_census_median_year_built = $censusYearUrl
        generated_at = $generatedAt
    }
}

$finalRows = $finalRows | Sort-Object metro_code

$jsonData = @{}
foreach ($row in $finalRows) {
    $jsonData[$row.metro_code] = @{
        metro_name = $row.metro_name
        msa_name = $row.msa_name
        state_code = $row.state_code
        labor_mult = $row.labor_mult
        climate_zone = $row.climate_zone
        regional_risk = $row.regional_risk
        fema_major_disaster_10y = $row.fema_major_disaster_10y
        owner_occupancy_rate_pct = $row.owner_occupancy_rate_pct
        median_year_built = $row.median_year_built
        legacy_housing_age_years = $row.legacy_housing_age_years
        repair_pressure_index = $row.repair_pressure_index
        source_fema = $row.source_fema
        source_census_owner_occupancy = $row.source_census_owner_occupancy
        source_census_median_year_built = $row.source_census_median_year_built
        generated_at = $row.generated_at
    }
}

$jsonRoot = @{
    meta = @{
        title = "2026 Metro Unique Signals (FEMA + Census + Internal Labor Index)"
        geography_unit = "MSA"
        generated_at = $generatedAt
        total_metros = $finalRows.Count
        fema_window_start = "2016-01-01"
        census_dataset = "ACS 5-year 2024"
        source_fema = $femaUrl
        source_census_owner_occupancy = $censusOwnerUrl
        source_census_median_year_built = $censusYearUrl
    }
    data = $jsonData
}

$jsonDir = Split-Path -Path $OutputJsonPath -Parent
$csvDir = Split-Path -Path $OutputCsvPath -Parent
$staticJsonDir = Split-Path -Path $OutputStaticJsonPath -Parent

New-Item -ItemType Directory -Path $jsonDir -Force | Out-Null
New-Item -ItemType Directory -Path $csvDir -Force | Out-Null
New-Item -ItemType Directory -Path $staticJsonDir -Force | Out-Null

$jsonRoot | ConvertTo-Json -Depth 8 | Set-Content -Path $OutputJsonPath -Encoding UTF8
$finalRows | Export-Csv -Path $OutputCsvPath -NoTypeInformation -Encoding UTF8

# Keep a second CSV next to the app data JSON for developer workflows.
$csvForAppDataPath = "src/main/resources/data/external/metro_unique_signals_2026.csv"
New-Item -ItemType Directory -Path (Split-Path -Path $csvForAppDataPath -Parent) -Force | Out-Null
$finalRows | Export-Csv -Path $csvForAppDataPath -NoTypeInformation -Encoding UTF8

# Public JSON copy for transparency/downloads.
$jsonRoot | ConvertTo-Json -Depth 8 | Set-Content -Path $OutputStaticJsonPath -Encoding UTF8

Write-Host ("Generated unique metro signal files for {0} metros." -f $finalRows.Count)
Write-Host ("JSON: {0}" -f $OutputJsonPath)
Write-Host ("CSV : {0}" -f $OutputCsvPath)
