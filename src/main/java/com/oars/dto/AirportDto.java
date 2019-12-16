package com.oars.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class AirportDto {
    private Long id;
    private String name;
    private String alias;
}
