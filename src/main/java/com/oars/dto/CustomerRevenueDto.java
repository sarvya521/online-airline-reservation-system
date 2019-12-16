package com.oars.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class CustomerRevenueDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long totalRevenue;
}
