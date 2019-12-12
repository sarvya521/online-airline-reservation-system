package com.oars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AirportDto {
    private Long id;
    private String name;
    private String alias;
}
