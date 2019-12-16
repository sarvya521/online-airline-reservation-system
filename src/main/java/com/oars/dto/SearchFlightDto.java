package com.oars.dto;

import com.oars.constant.SearchFlightConstants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SearchFlightDto {
    private Long sourceAirportId;
    private Long destinationAirportId;
    private LocalDate travelDate;
    private LocalDate returnDate;
    private SearchFlightConstants.SeatPreference seatPreference;
    private SearchFlightConstants.SortBy sortBy;
    private Integer minPrice;
    private Integer maxPrice;
    private List<String> airlines = new ArrayList<>();
}
