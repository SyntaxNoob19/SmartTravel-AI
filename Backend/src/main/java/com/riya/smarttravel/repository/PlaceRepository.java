package com.riya.smarttravel.repository;

import com.riya.smarttravel.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, String> {

    @Query("""
        select p.city as city, min(p.state) as state, min(p.region) as region, count(p) as placeCount
        from Place p
        where lower(p.region) = lower(:region)
        group by p.city
        order by count(p) desc
        """)
    List<CitySummaryProjection> findCitySummariesByRegion(@Param("region") String region);

    @Query("""
        select p.city as city, min(p.state) as state, min(p.region) as region, count(p) as placeCount
        from Place p
        where lower(p.category) = lower(:category)
        group by p.city
        order by count(p) desc
        """)
    List<CitySummaryProjection> findCitySummariesByCategory(@Param("category") String category);

    @Query("""
        select p.city as city, min(p.state) as state, min(p.region) as region, count(p) as placeCount
        from Place p
        where lower(p.moodTags) like lower(concat('%', :mood, '%'))
        group by p.city
        order by count(p) desc
        """)
    List<CitySummaryProjection> findCitySummariesByMood(@Param("mood") String mood);

    @Query("""
        select p.city as city, min(p.state) as state, min(p.region) as region, count(p) as placeCount
        from Place p
        where lower(p.bestTimeToVisit) like lower(concat('%', :season, '%'))
        group by p.city
        order by count(p) desc
        """)
    List<CitySummaryProjection> findCitySummariesBySeason(@Param("season") String season);

    @Query("""
        select p.city as city, min(p.state) as state, min(p.region) as region, count(p) as placeCount
        from Place p
        where p.recommendedDurationHours = :hours
        group by p.city
        order by count(p) desc
        """)
    List<CitySummaryProjection> findCitySummariesByDuration(@Param("hours") double hours);

    @Query("""
        select p.city as city, min(p.state) as state, min(p.region) as region, count(p) as placeCount
        from Place p
        group by p.city
        order by count(p) desc
        """)
    List<CitySummaryProjection> findTrendingCities();

    List<Place> findByCityContainingIgnoreCase(String city);

    @Query("""
        select p from Place p
        where lower(p.placeName) like lower(concat('%', :query, '%'))
           or lower(p.city) like lower(concat('%', :query, '%'))
           or lower(p.category) like lower(concat('%', :query, '%'))
           or lower(p.moodTags) like lower(concat('%', :query, '%'))
           or lower(p.description) like lower(concat('%', :query, '%'))
           or lower(p.significance) like lower(concat('%', :query, '%'))
           or lower(p.foodSpecialty) like lower(concat('%', :query, '%'))
           or lower(p.priority) like lower(concat('%', :query, '%'))
           or lower(p.seasonalHighlight) like lower(concat('%', :query, '%'))
        """)
    List<Place> searchPlaces(@Param("query") String query);

    @Query("""
        select p from Place p
        where (:region is null or lower(p.region) = lower(:region))
          and (:category is null or lower(p.category) = lower(:category))
          and (:mood is null or lower(p.moodTags) like lower(concat('%', :mood, '%')))
          and (:budgetLevel is null or lower(p.budgetLevel) = lower(:budgetLevel))
          and (:crowdLevel is null or lower(p.crowdLevel) = lower(:crowdLevel))
          and (:familyFriendly is null or p.familyFriendly = :familyFriendly)
          and (:priority is null or lower(p.priority) = lower(:priority))
          and (:season is null or lower(p.bestTimeToVisit) like lower(concat('%', :season, '%')))
          and (:weatherType is null or lower(p.weatherType) = lower(:weatherType))
          and (:minRating is null or p.rating >= :minRating)
        """)
    List<Place> smartFilter(
            @Param("region") String region,
            @Param("category") String category,
            @Param("mood") String mood,
            @Param("budgetLevel") String budgetLevel,
            @Param("crowdLevel") String crowdLevel,
            @Param("familyFriendly") Boolean familyFriendly,
            @Param("priority") String priority,
            @Param("season") String season,
            @Param("weatherType") String weatherType,
            @Param("minRating") Double minRating
    );

    Optional<Place> findByPlaceId(String placeId);
}
