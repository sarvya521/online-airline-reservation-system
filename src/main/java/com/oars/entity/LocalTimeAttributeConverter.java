package com.oars.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalTime;

@Converter(autoApply = true)
public class LocalTimeAttributeConverter implements AttributeConverter<LocalTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalTime localTime) {
        return localTime == null ? null : localTime.toString();
    }

    @Override
    public LocalTime convertToEntityAttribute(String sqlTime) {
        return sqlTime == null ? null : LocalTime.parse(sqlTime);
    }
}