package com.oars.dao;

import com.oars.entity.Booking;
import com.oars.entity.Flight;
import com.oars.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByUser(User user);

    List<Booking> findAllByBookingDateBetween(Date from, Date to);

    List<Booking> findAllByFlight(Flight flight);

    List<Booking> findAllByAirline(String airline);

    @Query(value =
            "SELECT u AS user, SUM(b.cost) AS totalRevenue " +
                    "FROM Booking b " +
                    "INNER JOIN b.user u " +
                    "GROUP BY u.id")
    List<CustomerRevenue> findCustomersWithTheirTotalRevenue();

    @Query(value =
            "SELECT f AS flight, COUNT(b.id) AS totalBookings " +
                    "FROM Booking b " +
                    "INNER JOIN b.flight f " +
                    "GROUP BY f.id")
    List<FlightBooking> findFlightsWithTotalBookings();

    public static interface CustomerRevenue {
        User getUser();

        Long getTotalRevenue();
    }

    public static interface FlightBooking {
        Flight getFlight();

        Long getTotalBookings();
    }
}
