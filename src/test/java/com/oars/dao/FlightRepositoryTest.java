package com.oars.dao;

import com.oars.entity.Aircraft;
import com.oars.entity.Airport;
import com.oars.entity.Flight;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class FlightRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private FlightRepository flightRepository;


    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }


    @Test
    void testFindByTravelDateAndDepartureFromAndArrivalAt() {
        Aircraft aircraft = new Aircraft();
        aircraft.setCode("XXX123");
        aircraft.setModel("Airbus 320");
        aircraft.setTotalBusinessSeats(10);
        aircraft.setTotalFirstclassSeats(10);
        aircraft.setTotalEconomySeats(20);
        aircraft = testEntityManager.persistAndFlush(aircraft);

        Airport departureFrom = new Airport();
        departureFrom.setName("AAA");
        departureFrom.setAlias("aaa");
        departureFrom = testEntityManager.persistAndFlush(departureFrom);

        Airport arrivalAt = new Airport();
        arrivalAt.setName("BBBB");
        arrivalAt.setAlias("bbb");
        arrivalAt = testEntityManager.persistAndFlush(arrivalAt);

        LocalDate date = LocalDate.now();

        Flight flight1 = new Flight();
        flight1.setTravelDate(date);
        flight1.setDepartureTime(LocalTime.now());
        flight1.setArrivalTime(LocalTime.now());
        flight1.setDepartureFrom(departureFrom);
        flight1.setArrivalAt(arrivalAt);
        flight1.setBusinessClassFare(100);
        flight1.setFirstclassFare(1000);
        flight1.setEconomyClassFare(5);
        flight1.setAirline("Lufthansa");
        flight1.setAircraft(aircraft);
        flight1 = testEntityManager.persistAndFlush(flight1);

        Flight flight2 = new Flight();
        flight2.setTravelDate(date);
        flight2.setDepartureTime(LocalTime.now());
        flight2.setArrivalTime(LocalTime.now());
        flight2.setDepartureFrom(departureFrom);
        flight2.setArrivalAt(arrivalAt);
        flight2.setBusinessClassFare(100);
        flight2.setFirstclassFare(1000);
        flight2.setEconomyClassFare(5);
        flight2.setAirline("Emirates");
        flight2.setAircraft(aircraft);
        flight2 = testEntityManager.persistAndFlush(flight2);

        List<Flight> flights =
                flightRepository.findByTravelDateAndDepartureFromAndArrivalAt(date, departureFrom, arrivalAt);
        assertNotNull(flights);
        assertEquals(2, flights.size());
    }
}