package com.riya.smarttravel.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class CsvImportTool {

    private static final Pattern SEASON_PATTERN =
            Pattern.compile("^(oct|nov|dec|jan|feb|mar|apr|may|jun|jul|aug|sep|year)", Pattern.CASE_INSENSITIVE);

    private static final String SCHEMA_SQL = """
            DROP TABLE IF EXISTS places;

            CREATE TABLE places (
                place_id VARCHAR(20) PRIMARY KEY,
                place_name VARCHAR(255) NOT NULL,
                city VARCHAR(120),
                state VARCHAR(120),
                region VARCHAR(50),
                place_type VARCHAR(100),
                category VARCHAR(100),
                significance VARCHAR(255),
                description TEXT,
                mood_tags VARCHAR(255),
                best_time_to_visit VARCHAR(100),
                ideal_visit_time VARCHAR(100),
                recommended_duration_hours DOUBLE,
                entry_fee DOUBLE,
                rating DOUBLE,
                crowd_level VARCHAR(50),
                family_friendly BOOLEAN,
                adventure_level VARCHAR(50),
                cultural_value VARCHAR(50),
                nature_value VARCHAR(50),
                budget_level VARCHAR(50),
                latitude DOUBLE,
                longitude DOUBLE,
                nearest_airport VARCHAR(255),
                nearest_railway VARCHAR(255),
                local_tips TEXT,
                food_specialty VARCHAR(255),
                safety_score DOUBLE,
                cleanliness_score DOUBLE,
                photography_spots VARCHAR(50),
                weather_type VARCHAR(50),
                seasonal_highlight VARCHAR(255),
                priority VARCHAR(50)
            )
            """;

    private static final String INSERT_SQL = """
            INSERT INTO places (
                place_id, place_name, city, state, region, place_type, category, significance,
                description, mood_tags, best_time_to_visit, ideal_visit_time, recommended_duration_hours,
                entry_fee, rating, crowd_level, family_friendly, adventure_level, cultural_value,
                nature_value, budget_level, latitude, longitude, nearest_airport, nearest_railway,
                local_tips, food_specialty, safety_score, cleanliness_score, photography_spots,
                weather_type, seasonal_highlight, priority
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                place_name = VALUES(place_name),
                city = VALUES(city),
                state = VALUES(state),
                region = VALUES(region),
                place_type = VALUES(place_type),
                category = VALUES(category),
                significance = VALUES(significance),
                description = VALUES(description),
                mood_tags = VALUES(mood_tags),
                best_time_to_visit = VALUES(best_time_to_visit),
                ideal_visit_time = VALUES(ideal_visit_time),
                recommended_duration_hours = VALUES(recommended_duration_hours),
                entry_fee = VALUES(entry_fee),
                rating = VALUES(rating),
                crowd_level = VALUES(crowd_level),
                family_friendly = VALUES(family_friendly),
                adventure_level = VALUES(adventure_level),
                cultural_value = VALUES(cultural_value),
                nature_value = VALUES(nature_value),
                budget_level = VALUES(budget_level),
                latitude = VALUES(latitude),
                longitude = VALUES(longitude),
                nearest_airport = VALUES(nearest_airport),
                nearest_railway = VALUES(nearest_railway),
                local_tips = VALUES(local_tips),
                food_specialty = VALUES(food_specialty),
                safety_score = VALUES(safety_score),
                cleanliness_score = VALUES(cleanliness_score),
                photography_spots = VALUES(photography_spots),
                weather_type = VALUES(weather_type),
                seasonal_highlight = VALUES(seasonal_highlight),
                priority = VALUES(priority)
            """;

    private CsvImportTool() {
    }

    public static void main(String[] args) throws Exception {
        Path csvPath = resolveCsvPath(args);
        List<PlaceRow> rows = loadRows(csvPath);

        String host = envOrDefault("DB_HOST", "localhost");
        String dbName = envOrDefault("DB_NAME", "smart_travel");
        String username = envOrDefault("DB_USERNAME", "root");
        String password = envOrDefault("DB_PASSWORD", "root");
        String url = "jdbc:mysql://" + host + ":3306/" + dbName;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            runSchema(connection);
            insertRows(connection, rows);
        }

        System.out.println("Imported " + rows.size() + " places from " + csvPath);
    }

    private static Path resolveCsvPath(String[] args) {
        List<Path> candidates = new ArrayList<>();
        if (args.length > 0 && args[0] != null && !args[0].isBlank()) {
            candidates.add(Paths.get(args[0]));
        }

        String envPath = System.getenv("SMARTTRAVEL_CSV_PATH");
        if (envPath != null && !envPath.isBlank()) {
            candidates.add(Paths.get(envPath));
        }

        candidates.add(Paths.get("india_travel_dataset_cleaned_v2.csv"));
        candidates.add(Paths.get("..", "india_travel_dataset_cleaned_v2.csv"));
        candidates.add(Paths.get("datasets", "india_travel_dataset_cleaned_v2.csv"));
        candidates.add(Paths.get("..", "datasets", "india_travel_dataset_cleaned_v2.csv"));
        candidates.add(Paths.get("C:\\Users\\Riya\\Downloads\\india_travel_dataset_cleaned_v2.csv"));

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate.normalize().toAbsolutePath();
            }
        }

        throw new IllegalStateException("Could not find india_travel_dataset_cleaned_v2.csv");
    }

    private static List<PlaceRow> loadRows(Path csvPath) throws IOException {
        try (Reader reader = Files.newBufferedReader(csvPath);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setQuote('"')
                     .get()
                     .parse(reader)) {
            List<PlaceRow> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                Map<String, String> normalized = normalizeRecord(record.toMap());
                rows.add(toPlaceRow(normalized));
            }
            return rows;
        }
    }

    private static Map<String, String> normalizeRecord(Map<String, String> record) {
        return looksLikeShiftedRow(record) ? repairShiftedRow(record) : record;
    }

    private static boolean looksLikeShiftedRow(Map<String, String> row) {
        String recommendedDuration = row.get("recommended_duration_hours");
        SeasonTime split = splitSeasonAndTime(recommendedDuration);
        return !looksLikeFloat(recommendedDuration)
                && (looksLikeSeason(row.get("ideal_visit_time")) || split != null);
    }

    private static Map<String, String> repairShiftedRow(Map<String, String> row) {
        DescriptionMood descriptionMood = extractDescriptionMood(row.get("description"));
        String currentMood = toText(row.get("mood_tags"));
        String currentBestTime = toText(row.get("best_time_to_visit"));
        String currentIdealTime = toText(row.get("ideal_visit_time"));
        String currentDuration = toText(row.get("recommended_duration_hours"));

        List<String> moodParts = new ArrayList<>();
        if (descriptionMood.mood() != null) {
            moodParts.add(descriptionMood.mood());
        }
        if (currentMood != null) {
            moodParts.add(currentMood);
        }

        String bestTimeToVisit;
        String idealVisitTime;

        if (looksLikeSeason(currentIdealTime)) {
            if (currentBestTime != null) {
                moodParts.add(currentBestTime);
            }
            bestTimeToVisit = currentIdealTime;
            idealVisitTime = currentDuration;
        } else {
            if (currentBestTime != null) {
                moodParts.add(currentBestTime);
            }
            if (currentIdealTime != null) {
                moodParts.add(currentIdealTime);
            }
            SeasonTime split = splitSeasonAndTime(currentDuration);
            if (split == null) {
                throw new IllegalStateException("Cannot repair shifted row for place_id=" + row.get("place_id"));
            }
            bestTimeToVisit = split.season();
            idealVisitTime = split.idealTime();
        }

        Map<String, String> repaired = new HashMap<>();
        repaired.put("place_id", row.get("place_id"));
        repaired.put("place_name", row.get("place_name"));
        repaired.put("city", row.get("city"));
        repaired.put("state", row.get("state"));
        repaired.put("region", row.get("region"));
        repaired.put("place_type", row.get("place_type"));
        repaired.put("category", row.get("category"));
        repaired.put("significance", row.get("significance"));
        repaired.put("description", descriptionMood.description());
        repaired.put("mood_tags", String.join(",", moodParts));
        repaired.put("best_time_to_visit", bestTimeToVisit);
        repaired.put("ideal_visit_time", idealVisitTime);
        repaired.put("recommended_duration_hours", row.get("entry_fee"));
        repaired.put("entry_fee", row.get("rating"));
        repaired.put("rating", row.get("crowd_level"));
        repaired.put("crowd_level", row.get("family_friendly"));
        repaired.put("family_friendly", row.get("adventure_level"));
        repaired.put("adventure_level", row.get("cultural_value"));
        repaired.put("cultural_value", row.get("nature_value"));
        repaired.put("nature_value", row.get("budget_level"));
        repaired.put("budget_level", null);
        repaired.put("latitude", row.get("latitude"));
        repaired.put("longitude", row.get("longitude"));
        repaired.put("nearest_airport", row.get("nearest_airport"));
        repaired.put("nearest_railway", row.get("nearest_railway"));
        repaired.put("local_tips", row.get("local_tips"));
        repaired.put("food_specialty", row.get("food_specialty"));
        repaired.put("safety_score", row.get("safety_score"));
        repaired.put("cleanliness_score", row.get("cleanliness_score"));
        repaired.put("photography_spots", row.get("photography_spots"));
        repaired.put("weather_type", row.get("weather_type"));
        repaired.put("seasonal_highlight", row.get("seasonal_highlight"));
        repaired.put("priority", row.get("priority"));
        return repaired;
    }

    private static DescriptionMood extractDescriptionMood(String descriptionValue) {
        String description = toText(descriptionValue);
        if (description == null || !description.contains(",")) {
            return new DescriptionMood(description, null);
        }

        int index = description.lastIndexOf(',');
        String base = description.substring(0, index).trim();
        String suffix = description.substring(index + 1).trim();
        if (suffix.contains(" ")) {
            return new DescriptionMood(description, null);
        }
        return new DescriptionMood(base, suffix);
    }

    private static SeasonTime splitSeasonAndTime(String value) {
        String cleaned = toText(value);
        if (cleaned == null || !cleaned.contains(" ")) {
            return null;
        }

        int index = cleaned.lastIndexOf(' ');
        String season = cleaned.substring(0, index).trim();
        String idealTime = cleaned.substring(index + 1).trim();
        return looksLikeSeason(season) ? new SeasonTime(season, idealTime) : null;
    }

    private static PlaceRow toPlaceRow(Map<String, String> row) {
        return new PlaceRow(
                toText(row.get("place_id")),
                toText(row.get("place_name")),
                toText(row.get("city")),
                toText(row.get("state")),
                toText(row.get("region")),
                toText(row.get("place_type")),
                toText(row.get("category")),
                toText(row.get("significance")),
                toText(row.get("description")),
                toText(row.get("mood_tags")),
                toText(row.get("best_time_to_visit")),
                toText(row.get("ideal_visit_time")),
                toDouble(row.get("recommended_duration_hours")),
                toDouble(row.get("entry_fee")),
                toDouble(row.get("rating")),
                toText(row.get("crowd_level")),
                toBoolean(row.get("family_friendly")),
                toText(row.get("adventure_level")),
                toText(row.get("cultural_value")),
                toText(row.get("nature_value")),
                toText(row.get("budget_level")),
                toDouble(row.get("latitude")),
                toDouble(row.get("longitude")),
                toText(row.get("nearest_airport")),
                toText(row.get("nearest_railway")),
                toText(row.get("local_tips")),
                toText(row.get("food_specialty")),
                toDouble(row.get("safety_score")),
                toDouble(row.get("cleanliness_score")),
                toText(row.get("photography_spots")),
                toText(row.get("weather_type")),
                toText(row.get("seasonal_highlight")),
                toText(row.get("priority"))
        );
    }

    private static void runSchema(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (String sql : SCHEMA_SQL.split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        }
    }

    private static void insertRows(Connection connection, List<PlaceRow> rows) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            for (PlaceRow row : rows) {
                bindString(statement, 1, row.placeId());
                bindString(statement, 2, row.placeName());
                bindString(statement, 3, row.city());
                bindString(statement, 4, row.state());
                bindString(statement, 5, row.region());
                bindString(statement, 6, row.placeType());
                bindString(statement, 7, row.category());
                bindString(statement, 8, row.significance());
                bindString(statement, 9, row.description());
                bindString(statement, 10, row.moodTags());
                bindString(statement, 11, row.bestTimeToVisit());
                bindString(statement, 12, row.idealVisitTime());
                bindDouble(statement, 13, row.recommendedDurationHours());
                bindDouble(statement, 14, row.entryFee());
                bindDouble(statement, 15, row.rating());
                bindString(statement, 16, row.crowdLevel());
                bindBoolean(statement, 17, row.familyFriendly());
                bindString(statement, 18, row.adventureLevel());
                bindString(statement, 19, row.culturalValue());
                bindString(statement, 20, row.natureValue());
                bindString(statement, 21, row.budgetLevel());
                bindDouble(statement, 22, row.latitude());
                bindDouble(statement, 23, row.longitude());
                bindString(statement, 24, row.nearestAirport());
                bindString(statement, 25, row.nearestRailway());
                bindString(statement, 26, row.localTips());
                bindString(statement, 27, row.foodSpecialty());
                bindDouble(statement, 28, row.safetyScore());
                bindDouble(statement, 29, row.cleanlinessScore());
                bindString(statement, 30, row.photographySpots());
                bindString(statement, 31, row.weatherType());
                bindString(statement, 32, row.seasonalHighlight());
                bindString(statement, 33, row.priority());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static void bindString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

    private static void bindDouble(PreparedStatement statement, int index, Double value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.DOUBLE);
        } else {
            statement.setDouble(index, value);
        }
    }

    private static void bindBoolean(PreparedStatement statement, int index, Boolean value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BOOLEAN);
        } else {
            statement.setBoolean(index, value);
        }
    }

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String toText(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    private static Double toDouble(String value) {
        String cleaned = toText(value);
        if (cleaned == null) {
            return null;
        }
        String normalized = cleaned.toLowerCase(Locale.ROOT);
        if (normalized.equals("free") || normalized.equals("no fee") || normalized.equals("nil") || normalized.equals("none")) {
            return 0.0;
        }
        return Double.parseDouble(cleaned);
    }

    private static Boolean toBoolean(String value) {
        String cleaned = toText(value);
        if (cleaned == null) {
            return null;
        }
        String normalized = cleaned.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "yes", "true", "1" -> true;
            case "no", "false", "0" -> false;
            default -> throw new IllegalStateException("Unsupported boolean value: " + value);
        };
    }

    private static boolean looksLikeFloat(String value) {
        try {
            return toText(value) != null && Double.parseDouble(toText(value)) >= 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static boolean looksLikeSeason(String value) {
        String cleaned = toText(value);
        return cleaned != null && SEASON_PATTERN.matcher(cleaned).find();
    }

    private record DescriptionMood(String description, String mood) {
    }

    private record SeasonTime(String season, String idealTime) {
    }

    private record PlaceRow(
            String placeId,
            String placeName,
            String city,
            String state,
            String region,
            String placeType,
            String category,
            String significance,
            String description,
            String moodTags,
            String bestTimeToVisit,
            String idealVisitTime,
            Double recommendedDurationHours,
            Double entryFee,
            Double rating,
            String crowdLevel,
            Boolean familyFriendly,
            String adventureLevel,
            String culturalValue,
            String natureValue,
            String budgetLevel,
            Double latitude,
            Double longitude,
            String nearestAirport,
            String nearestRailway,
            String localTips,
            String foodSpecialty,
            Double safetyScore,
            Double cleanlinessScore,
            String photographySpots,
            String weatherType,
            String seasonalHighlight,
            String priority
    ) {
    }
}
