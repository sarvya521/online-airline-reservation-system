package com.oars.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "flight")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Flight implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "travel_date")
    private LocalDate travelDate;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @ManyToOne
    @JoinColumn(name = "departure_from")
    private Airport departureFrom;

    @ManyToOne
    @JoinColumn(name = "arrival_at")
    private Airport arrivalAt;

    @Column(name = "business_class_fare")
    private Integer businessClassFare;

    @Column(name = "firstclass_fare")
    private Integer firstclassFare;

    @Column(name = "economy_class_fare")
    private Integer economyClassFare;

    @Column(name = "remaining_business_seats")
    private Integer remainingBusinessSeats;

    @Column(name = "remaining_firstclass_seats")
    private Integer remainingFirstclassSeats;

    @Column(name = "remaining_economy_seats")
    private Integer remainingEconomySeats;

    @Column(name = "airline")
    private String airline;

    @ManyToOne
    @JoinColumn(name = "fk_aircraft_id")
    private Aircraft aircraft;

    @OneToMany(mappedBy = "flight")
    private Set<Booking> bookings = new HashSet<>();
}
