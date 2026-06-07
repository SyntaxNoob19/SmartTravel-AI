package com.riya.smarttravel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riya.smarttravel.dto.HolidayDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HolidayService {

    private static final String NAGER_API = "https://date.nager.at/api/v3";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get public holidays for a year and country
     */
    @Cacheable(value = "holidays", key = "#year + '_' + #countryCode")
    public List<HolidayDto> getHolidaysByYear(int year, String countryCode) {
        try {
            String normalizedCountryCode = normalizeCountryCode(countryCode);
            String url = String.format("%s/PublicHolidays/%d/%s", NAGER_API, year, normalizedCountryCode);
            List<HolidayDto> holidays = fetchHolidays(url);

            if (holidays.isEmpty() && "IN".equals(normalizedCountryCode)) {
                log.warn("Nager.Date returned no holidays for IN; using fallback holiday set for year {}", year);
                return getFallbackIndiaHolidays(year);
            }
            return holidays;
        } catch (Exception e) {
            log.error("Error fetching holidays for year {} and country {}", year, countryCode, e);
            if ("IN".equals(normalizeCountryCode(countryCode))) {
                return getFallbackIndiaHolidays(year);
            }
            return new ArrayList<>();
        }
    }

    /**
     * Get next public holidays across countries
     */
    @Cacheable(value = "upcoming-holidays")
    public List<HolidayDto> getUpcomingHolidays() {
        try {
            String url = String.format("%s/NextPublicHolidaysCross", NAGER_API);
            Request request = new Request.Builder().url(url).build();
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("Failed to fetch upcoming holidays: {}", response.code());
                return new ArrayList<>();
            }

            JsonNode data = objectMapper.readTree(response.body().string());
            List<HolidayDto> holidays = new ArrayList<>();

            if (data.isArray()) {
                for (JsonNode node : data) {
                    HolidayDto holiday = new HolidayDto();
                    holiday.setDate(LocalDate.parse(node.get("date").asText()));
                    holiday.setName(node.get("name").asText());
                    holiday.setCountryCode(node.get("countryCode").asText());
                    holiday.setType(node.get("type").asText());
                    holidays.add(holiday);
                }
            }

            return holidays;

        } catch (Exception e) {
            log.error("Error fetching upcoming holidays", e);
            return getFallbackIndiaHolidays(LocalDate.now().getYear());
        }
    }

    /**
     * Check for long weekends in a given month
     */
    @Cacheable(value = "long-weekends", key = "#year + '_' + #month + '_' + #countryCode")
    public List<HolidayDto> getLongWeekends(int year, int month, String countryCode) {
        List<HolidayDto> holidays = getHolidaysByYear(year, countryCode);
        List<HolidayDto> longWeekends = new ArrayList<>();

        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

        for (HolidayDto holiday : holidays) {
            if (holiday.getDate().isAfter(monthStart.minusDays(2)) && 
                holiday.getDate().isBefore(monthEnd.plusDays(2))) {
                
                // Check if it's a Friday or Monday (creates long weekend with weekends)
                int dayOfWeek = holiday.getDate().getDayOfWeek().getValue();
                if (dayOfWeek == 5 || dayOfWeek == 1) { // Friday=5, Monday=1
                    holiday.setIsLongWeekend(true);
                    longWeekends.add(holiday);
                }
            }
        }

        return longWeekends;
    }

    /**
     * Get holidays for current year
     */
    public List<HolidayDto> getHolidaysForCurrentYear(String countryCode) {
        return getHolidaysByYear(LocalDate.now().getYear(), countryCode);
    }

    private List<HolidayDto> fetchHolidays(String url) throws Exception {
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            log.error("Failed to fetch holidays: {}", response.code());
            return new ArrayList<>();
        }

        JsonNode data = objectMapper.readTree(response.body().string());
        List<HolidayDto> holidays = new ArrayList<>();

        if (data.isArray()) {
            for (JsonNode node : data) {
                HolidayDto holiday = new HolidayDto();
                holiday.setDate(LocalDate.parse(node.get("date").asText()));
                holiday.setName(node.get("name").asText());
                holiday.setType(node.get("types").get(0).asText());
                holiday.setCountryCode(node.get("countryCode").asText());
                holidays.add(holiday);
            }
        }

        return holidays;
    }

    private String normalizeCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            return "IN";
        }
        return countryCode.trim().toUpperCase();
    }

    private List<HolidayDto> getFallbackIndiaHolidays(int year) {
        List<HolidayDto> holidays = new ArrayList<>();

        holidays.add(createHoliday(year, 1, 1, "New Year's Day", "Observance", "IN"));
        holidays.add(createHoliday(year, 1, 13, "Lohri", "Festival", "IN"));
        holidays.add(createHoliday(year, 1, 14, "Makar Sankranti / Pongal", "Festival", "IN"));
        holidays.add(createHoliday(year, 1, 26, "Republic Day", "Public", "IN"));
        holidays.add(createHoliday(year, 4, 14, "Dr. B. R. Ambedkar Jayanti", "Public", "IN"));
        holidays.add(createHoliday(year, 5, 1, "Labour Day", "Public", "IN"));
        holidays.add(createHoliday(year, 8, 15, "Independence Day", "Public", "IN"));
        holidays.add(createHoliday(year, 10, 2, "Gandhi Jayanti", "Public", "IN"));
        holidays.add(createHoliday(year, 11, 14, "Children's Day", "Observance", "IN"));
        holidays.add(createHoliday(year, 11, 26, "Constitution Day", "Observance", "IN"));
        holidays.add(createHoliday(year, 12, 25, "Christmas Day", "Public", "IN"));

        return holidays;
    }

    private HolidayDto createHoliday(int year, int month, int day, String name, String type, String countryCode) {
        HolidayDto holiday = new HolidayDto();
        holiday.setDate(LocalDate.of(year, month, day));
        holiday.setName(name);
        holiday.setType(type);
        holiday.setCountryCode(countryCode);
        return holiday;
    }
}
