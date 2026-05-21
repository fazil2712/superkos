package com.superkos.app.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA AttributeConverter that serializes List<Integer> to/from a JSON array string.
 * e.g.  [5, 7, 3, 8, ...]  stored as  "[5,7,3,8,...]"  in the database TEXT column.
 */
@Converter
public class IntegerListConverter implements AttributeConverter<List<Integer>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Integer> list) {
        if (list == null || list.isEmpty()) return "[]";
        try {
            return MAPPER.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return new ArrayList<>();
        try {
            return MAPPER.readValue(dbData, new TypeReference<List<Integer>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
