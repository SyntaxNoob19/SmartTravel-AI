package com.riya.smarttravel;

import com.riya.smarttravel.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmarttravelApplicationTests {

    @Autowired
    private PlaceRepository repository;

    @Test
    void printKolkataPlaces() {
        repository.findAll().forEach(place -> {
            if (place.getCity() != null && place.getCity().toLowerCase().contains("kol")) {
                System.out.println("DB_PLACE: " + place.getPlaceId() + " | " + place.getPlaceName() + " | " + place.getCity() + " | " + place.getState());
            }
        });
    }
}
