package com.oars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AircraftDto {
    private Long id;
    private String code;
    private String model;
    private Integer totalBusinessSeats;
    private Integer totalFirstclassSeats;
    private Integer totalEconomySeats;
}
