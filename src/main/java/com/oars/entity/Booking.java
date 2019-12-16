package com.oars.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "booking")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "pnr")
    private String pnr;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Column(name = "travel_date")
    private LocalDate travelDate;

    @Column(name = "seat_class")
    private String seatClass;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "airline")
    private String airline;

    @Column(name = "status")
    private String status;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "fk_flight_id")
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    private User user;
}
