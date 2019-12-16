package com.oars.constant;

import java.util.Arrays;
import java.util.List;

public class SearchFlightConstants {
    public static final List<String> ALLOWED_SEAT_PREFERENCES = Arrays.asList("ECONOMY", "BUSINESS", "FIRST");
    public static final List<String> ALLOWED_SORT_CRITERIA = Arrays.asList("PRICE_ASC", "PRICE_DESC",
            "DEPARTURE_ASC", "DEPARTURE_DESC",
            "ARRIVAL_ASC", "ARRIVAL_DESC");

    public enum SeatPreference {
        ECONOMY,
        BUSINESS,
        FIRST;
    }

    public enum SortBy {
        PRICE_ASC,
        PRICE_DESC,
        DEPARTURE_ASC,
        DEPARTURE_DESC,
        ARRIVAL_ASC,
        ARRIVAL_DESC
    }
}
