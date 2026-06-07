import csv
import os
from pathlib import Path
import re

import mysql.connector


def resolve_csv_path() -> Path:
    candidates = [
        os.environ.get("SMARTTRAVEL_CSV_PATH"),
        Path.cwd() / "india_travel_dataset_cleaned_v2.csv",
        Path(__file__).parent.parent / "datasets" / "india_travel_dataset_cleaned_v2.csv",
        Path(r"C:\Users\Riya\Downloads\india_travel_dataset_cleaned_v2.csv"),
    ]
    for candidate in candidates:
        if candidate is None:
            continue
        path = Path(candidate)
        if path.exists():
            return path
    raise FileNotFoundError("Could not find india_travel_dataset_cleaned_v2.csv")


def to_text(value: str | None) -> str | None:
    if value is None:
        return None
    cleaned = value.strip()
    return cleaned or None


def to_float(value: str | None) -> float | None:
    cleaned = to_text(value)
    if cleaned is None:
        return None

    normalized = cleaned.lower()
    if normalized in {"free", "no fee", "nil", "none"}:
        return 0.0

    return float(cleaned)


def to_bool(value: str | None) -> bool | None:
    cleaned = to_text(value)
    if cleaned is None:
        return None
    normalized = cleaned.lower()
    if normalized in {"yes", "true", "1"}:
        return True
    if normalized in {"no", "false", "0"}:
        return False
    raise ValueError(f"Unsupported boolean value: {value}")


SEASON_PATTERN = re.compile(r"^(oct|nov|dec|jan|feb|mar|apr|may|jun|jul|aug|sep|year)", re.IGNORECASE)


def looks_like_float(value: str | None) -> bool:
    cleaned = to_text(value)
    if cleaned is None:
        return False
    try:
        float(cleaned)
        return True
    except ValueError:
        return False


def looks_like_shifted_row(row: dict[str, str | None]) -> bool:
    recommended_duration = row.get("recommended_duration_hours")
    season_part, ideal_time_part = split_season_and_time(recommended_duration)
    return (
        not looks_like_float(recommended_duration)
        and (
            looks_like_season(row.get("ideal_visit_time"))
            or (season_part is not None and ideal_time_part is not None)
        )
    )


def looks_like_season(value: str | None) -> bool:
    cleaned = to_text(value)
    return cleaned is not None and SEASON_PATTERN.match(cleaned) is not None


def extract_description_mood(description: str | None) -> tuple[str | None, str | None]:
    cleaned = to_text(description)
    if cleaned is None or "," not in cleaned:
        return cleaned, None

    base, suffix = cleaned.rsplit(",", 1)
    suffix = suffix.strip()
    if " " in suffix:
        return cleaned, None
    return base.strip(), suffix


def split_season_and_time(value: str | None) -> tuple[str | None, str | None]:
    cleaned = to_text(value)
    if cleaned is None or " " not in cleaned:
        return None, None

    season, ideal_time = cleaned.rsplit(" ", 1)
    if looks_like_season(season):
        return season, ideal_time
    return None, None


def repair_shifted_row(row: dict[str, str | None]) -> dict[str, str | None]:
    fixed_description, description_mood = extract_description_mood(row.get("description"))
    current_mood = to_text(row.get("mood_tags"))
    current_best_time = to_text(row.get("best_time_to_visit"))
    current_ideal_time = to_text(row.get("ideal_visit_time"))
    current_duration = to_text(row.get("recommended_duration_hours"))

    mood_parts = [description_mood, current_mood]
    best_time_to_visit = None
    ideal_visit_time = None

    if looks_like_season(current_ideal_time):
        mood_parts.append(current_best_time)
        best_time_to_visit = current_ideal_time
        ideal_visit_time = current_duration
    else:
        mood_parts.extend([current_best_time, current_ideal_time])
        best_time_to_visit, ideal_visit_time = split_season_and_time(current_duration)

    if best_time_to_visit is None or ideal_visit_time is None:
        raise ValueError(f"Cannot repair shifted row for place_id={row.get('place_id')}")

    return {
        "place_id": row.get("place_id"),
        "place_name": row.get("place_name"),
        "city": row.get("city"),
        "state": row.get("state"),
        "region": row.get("region"),
        "place_type": row.get("place_type"),
        "category": row.get("category"),
        "significance": row.get("significance"),
        "description": fixed_description,
        "mood_tags": ",".join(filter(None, mood_parts)),
        "best_time_to_visit": best_time_to_visit,
        "ideal_visit_time": ideal_visit_time,
        "recommended_duration_hours": row.get("entry_fee"),
        "entry_fee": row.get("rating"),
        "rating": row.get("crowd_level"),
        "crowd_level": row.get("family_friendly"),
        "family_friendly": row.get("adventure_level"),
        "adventure_level": row.get("cultural_value"),
        "cultural_value": row.get("nature_value"),
        "nature_value": row.get("budget_level"),
        "budget_level": None,
        "latitude": row.get("latitude"),
        "longitude": row.get("longitude"),
        "nearest_airport": row.get("nearest_airport"),
        "nearest_railway": row.get("nearest_railway"),
        "local_tips": row.get("local_tips"),
        "food_specialty": row.get("food_specialty"),
        "safety_score": row.get("safety_score"),
        "cleanliness_score": row.get("cleanliness_score"),
        "photography_spots": row.get("photography_spots"),
        "weather_type": row.get("weather_type"),
        "seasonal_highlight": row.get("seasonal_highlight"),
        "priority": row.get("priority"),
    }


SCHEMA_SQL = """
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
);
"""


INSERT_SQL = """
INSERT INTO places (
    place_id, place_name, city, state, region, place_type, category, significance,
    description, mood_tags, best_time_to_visit, ideal_visit_time, recommended_duration_hours,
    entry_fee, rating, crowd_level, family_friendly, adventure_level, cultural_value,
    nature_value, budget_level, latitude, longitude, nearest_airport, nearest_railway,
    local_tips, food_specialty, safety_score, cleanliness_score, photography_spots,
    weather_type, seasonal_highlight, priority
) VALUES (
    %(place_id)s, %(place_name)s, %(city)s, %(state)s, %(region)s, %(place_type)s, %(category)s, %(significance)s,
    %(description)s, %(mood_tags)s, %(best_time_to_visit)s, %(ideal_visit_time)s, %(recommended_duration_hours)s,
    %(entry_fee)s, %(rating)s, %(crowd_level)s, %(family_friendly)s, %(adventure_level)s, %(cultural_value)s,
    %(nature_value)s, %(budget_level)s, %(latitude)s, %(longitude)s, %(nearest_airport)s, %(nearest_railway)s,
    %(local_tips)s, %(food_specialty)s, %(safety_score)s, %(cleanliness_score)s, %(photography_spots)s,
    %(weather_type)s, %(seasonal_highlight)s, %(priority)s
)
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
"""


def load_rows(csv_path: Path) -> list[dict]:
    rows: list[dict] = []
    with csv_path.open(newline="", encoding="utf-8") as handle:
        reader = csv.DictReader(handle)
        for row in reader:
            normalized_row = repair_shifted_row(row) if looks_like_shifted_row(row) else row
            rows.append({
                "place_id": to_text(normalized_row.get("place_id")),
                "place_name": to_text(normalized_row.get("place_name")),
                "city": to_text(normalized_row.get("city")),
                "state": to_text(normalized_row.get("state")),
                "region": to_text(normalized_row.get("region")),
                "place_type": to_text(normalized_row.get("place_type")),
                "category": to_text(normalized_row.get("category")),
                "significance": to_text(normalized_row.get("significance")),
                "description": to_text(normalized_row.get("description")),
                "mood_tags": to_text(normalized_row.get("mood_tags")),
                "best_time_to_visit": to_text(normalized_row.get("best_time_to_visit")),
                "ideal_visit_time": to_text(normalized_row.get("ideal_visit_time")),
                "recommended_duration_hours": to_float(normalized_row.get("recommended_duration_hours")),
                "entry_fee": to_float(normalized_row.get("entry_fee")),
                "rating": to_float(normalized_row.get("rating")),
                "crowd_level": to_text(normalized_row.get("crowd_level")),
                "family_friendly": to_bool(normalized_row.get("family_friendly")),
                "adventure_level": to_text(normalized_row.get("adventure_level")),
                "cultural_value": to_text(normalized_row.get("cultural_value")),
                "nature_value": to_text(normalized_row.get("nature_value")),
                "budget_level": to_text(normalized_row.get("budget_level")),
                "latitude": to_float(normalized_row.get("latitude")),
                "longitude": to_float(normalized_row.get("longitude")),
                "nearest_airport": to_text(normalized_row.get("nearest_airport")),
                "nearest_railway": to_text(normalized_row.get("nearest_railway")),
                "local_tips": to_text(normalized_row.get("local_tips")),
                "food_specialty": to_text(normalized_row.get("food_specialty")),
                "safety_score": to_float(normalized_row.get("safety_score")),
                "cleanliness_score": to_float(normalized_row.get("cleanliness_score")),
                "photography_spots": to_text(normalized_row.get("photography_spots")),
                "weather_type": to_text(normalized_row.get("weather_type")),
                "seasonal_highlight": to_text(normalized_row.get("seasonal_highlight")),
                "priority": to_text(normalized_row.get("priority")),
            })
    return rows


def main() -> None:
    csv_path = resolve_csv_path()
    rows = load_rows(csv_path)

    conn = mysql.connector.connect(
        host=os.environ.get("DB_HOST", "localhost"),
        user=os.environ.get("DB_USERNAME", "root"),
        password=os.environ.get("DB_PASSWORD", ""),
        database=os.environ.get("DB_NAME", "smart_travel"),
    )

    cursor = conn.cursor()
    for statement in [part.strip() for part in SCHEMA_SQL.split(";") if part.strip()]:
        cursor.execute(statement)

    cursor.executemany(INSERT_SQL, rows)
    conn.commit()
    cursor.close()
    conn.close()

    print(f"Imported {len(rows)} places from {csv_path}")


if __name__ == "__main__":
    main()
