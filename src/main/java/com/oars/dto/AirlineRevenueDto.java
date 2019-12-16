package com.oars.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(of = "name")
@NoArgsConstructor
public class AirlineRevenueDto {
    private String name;
    private Long totalRevenue;
}
