package com.backend.boilerplate.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@RequiredArgsConstructor
public enum Role {
    DEFAULT("DEFAULT"),
    SYSTEM_ADMIN("System Admin");

    @Getter
    private final String name;
}
