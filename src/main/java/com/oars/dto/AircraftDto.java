package com.oars.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class AircraftDto {
    private Long id;
    private String code;
    private String model;
    private Integer totalBusinessSeats;
    private Integer totalFirstclassSeats;
    private Integer totalEconomySeats;
}
