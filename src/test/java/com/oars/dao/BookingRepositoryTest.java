package com.oars.dao;

import com.oars.constant.Role;
import com.oars.entity.Aircraft;
import com.oars.entity.Airport;
import com.oars.entity.Booking;
import com.oars.entity.Flight;
import com.oars.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Time;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookingRepository bookingRepository;


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
    void findCustomerWithTheMostTotalRevenue() {
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

        Date date = new Date();

        Flight flight = new Flight();
        flight.setTravelDate(date);
        flight.setDepartureTime(new Time(date.getTime()));
        flight.setArrivalTime(new Time(date.getTime()));
        flight.setDepartureFrom(departureFrom);
        flight.setArrivalAt(arrivalAt);
        flight.setBusinessClassFare(100);
        flight.setFirstclassFare(1000);
        flight.setEconomyClassFare(5);
        flight.setAirline("Lufthansa");
        flight.setAircraft(aircraft);
        flight = testEntityManager.persistAndFlush(flight);

        User user1 = new User();
        user1.setEmail("abc@gmail.com");
        user1.setPassword("123456");
        user1.setRole(Role.CUSTOMER.name());
        user1 = testEntityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setEmail("abcd@gmail.com");
        user2.setPassword("1234567");
        user2.setRole(Role.CUSTOMER.name());
        user2 = testEntityManager.persistAndFlush(user2);

        Booking booking1 = new Booking();
        booking1.setPnr(randomAlphaNumeric(6));
        booking1.setBookingDate(date);
        booking1.setTravelDate(date);
        booking1.setSeatClass("BUSINESS");
        booking1.setCost(100);
        booking1.setAirline("Lufthansa");
        booking1.setFlight(flight);
        booking1.setUser(user1);
        booking1 = testEntityManager.persistAndFlush(booking1);

        Booking booking2 = new Booking();
        booking2.setPnr(randomAlphaNumeric(6));
        booking2.setBookingDate(date);
        booking2.setTravelDate(date);
        booking2.setSeatClass("FIRSTCLASS");
        booking2.setCost(1000);
        booking2.setAirline("Lufthansa");
        booking2.setFlight(flight);
        booking2.setUser(user2);
        booking2 = testEntityManager.persistAndFlush(booking2);

        List<BookingRepository.CustomerRevenue> customerRevenues =
                bookingRepository.findCustomersWithTheirTotalRevenue();
        assertNotNull(customerRevenues);
        customerRevenues.stream().sorted(Comparator.comparing(BookingRepository.CustomerRevenue::getTotalRevenue).reversed())
                .forEach(customerRevenue -> System.out.println(customerRevenue.getUser().getEmail() + " => " + customerRevenue.getTotalRevenue()));
    }

    @Test
    void findFlightsWithTotalBookings() {
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

        Date date = new Date();

        Flight flight1 = new Flight();
        flight1.setTravelDate(date);
        flight1.setDepartureTime(new Time(date.getTime()));
        flight1.setArrivalTime(new Time(date.getTime()));
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
        flight2.setDepartureTime(new Time(date.getTime()));
        flight2.setArrivalTime(new Time(date.getTime()));
        flight2.setDepartureFrom(departureFrom);
        flight2.setArrivalAt(arrivalAt);
        flight2.setBusinessClassFare(100);
        flight2.setFirstclassFare(1000);
        flight2.setEconomyClassFare(5);
        flight2.setAirline("Emirates");
        flight2.setAircraft(aircraft);
        flight2 = testEntityManager.persistAndFlush(flight2);

        User user1 = new User();
        user1.setEmail("abc@gmail.com");
        user1.setPassword("123456");
        user1.setRole(Role.CUSTOMER.name());
        user1 = testEntityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setEmail("abcd@gmail.com");
        user2.setPassword("1234567");
        user2.setRole(Role.CUSTOMER.name());
        user2 = testEntityManager.persistAndFlush(user2);

        Booking booking1 = new Booking();
        booking1.setPnr(randomAlphaNumeric(6));
        booking1.setBookingDate(date);
        booking1.setTravelDate(date);
        booking1.setSeatClass("BUSINESS");
        booking1.setCost(100);
        booking1.setAirline("Lufthansa");
        booking1.setFlight(flight1);
        booking1.setUser(user1);
        booking1 = testEntityManager.persistAndFlush(booking1);

        Booking booking2 = new Booking();
        booking2.setPnr(randomAlphaNumeric(6));
        booking2.setBookingDate(date);
        booking2.setTravelDate(date);
        booking2.setSeatClass("FIRSTCLASS");
        booking2.setCost(1000);
        booking2.setAirline("Lufthansa");
        booking2.setFlight(flight1);
        booking2.setUser(user2);
        booking2 = testEntityManager.persistAndFlush(booking2);

        Booking booking3 = new Booking();
        booking3.setPnr(randomAlphaNumeric(6));
        booking3.setBookingDate(date);
        booking3.setTravelDate(date);
        booking3.setSeatClass("FIRSTCLASS");
        booking3.setCost(1000);
        booking3.setAirline("Lufthansa");
        booking3.setFlight(flight2);
        booking3.setUser(user2);
        booking3 = testEntityManager.persistAndFlush(booking3);

        List<BookingRepository.FlightBooking> flightsWithTotalBookings =
                bookingRepository.findFlightsWithTotalBookings();
        assertNotNull(flightsWithTotalBookings);
        flightsWithTotalBookings.stream().sorted(Comparator.comparing(BookingRepository.FlightBooking::getTotalBookings).reversed())
                .forEach(customerRevenue -> System.out.println(customerRevenue.getFlight().getId() + " => " + customerRevenue.getTotalBookings()));
    }
}