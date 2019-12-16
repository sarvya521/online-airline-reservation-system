package com.oars.dao;

import com.oars.entity.Booking;
import com.oars.entity.Flight;
import com.oars.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByUserAndTravelDateLessThan(User user, LocalDate travelDate);

    List<Booking> findByUserAndTravelDateGreaterThanEqual(User user, LocalDate travelDate);

    List<Booking> findByBookingDateBetween(LocalDate from, LocalDate to);

    List<Booking> findByFlight(Flight flight);

    List<Booking> findByAirline(String airline);

    public static interface CustomerRevenue {
        User getUser();

        Long getTotalRevenue();
    }

    @Query(value =
            "SELECT u AS user, SUM(b.cost) AS totalRevenue " +
                    "FROM Booking b " +
                    "INNER JOIN b.user u " +
                    "GROUP BY u.id")
    List<CustomerRevenue> findCustomersWithTheirTotalRevenue();

    public static interface FlightBooking {
        Flight getFlight();

        Long getTotalBookings();
    }

    @Query(value =
            "SELECT f AS flight, COUNT(b.id) AS totalBookings " +
                    "FROM Booking b " +
                    "INNER JOIN b.flight f " +
                    "GROUP BY f.id")
    List<FlightBooking> findFlightsWithTotalBookings();

    public static interface AirlineRevenue {
        String getAirline();

        Long getTotalRevenue();
    }

    @Query(value =
            "SELECT b.airline AS airline, SUM(b.cost) AS totalRevenue " +
                    "FROM Booking b " +
                    "GROUP BY b.airline")
    List<AirlineRevenue> findAirlinesWithTheirTotalRevenue();

    public static interface FlightRevenue {
        Flight getFlight();

        Long getTotalRevenue();
    }

    @Query(value =
            "SELECT f AS flight, SUM(b.cost) AS totalRevenue " +
                    "FROM Booking b " +
                    "INNER JOIN b.flight f " +
                    "GROUP BY f.id")
    List<FlightRevenue> findFlightsWithTheirTotalRevenue();


}
