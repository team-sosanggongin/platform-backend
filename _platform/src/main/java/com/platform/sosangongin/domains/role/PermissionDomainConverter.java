package com.platform.sosangongin.domains.role;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PermissionDomainConverter implements AttributeConverter<PermissionDomain, String> {

    @Override
    public String convertToDatabaseColumn(PermissionDomain attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public PermissionDomain convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new PermissionDomain(dbData);
    }
}
