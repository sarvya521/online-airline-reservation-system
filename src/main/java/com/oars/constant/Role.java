package com.oars.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    ADMIN("ADMIN"),
    CUSTOMER_REPRESENTATIVE("CUSTOMER_REPRESENTATIVE"),
    CUSTOMER("CUSTOMER");

    @Getter
    private final String name;
}
