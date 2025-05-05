package com.atix.demo.service.extraction.impl;

import org.springframework.stereotype.Component;
import com.atix.demo.service.extraction.core.Formatter;

import java.util.*;

/**
 * Formatter responsible for formatting credit note data.
 * This class is annotated with {@code @Component} to indicate that it is a Spring component.
 */
@Component
public class ConcentrationMeanFormatter implements Formatter {

    /**
     * Retrieves a list of supported types that this formatter can handle.
     * @return a list of {@link String} representing the types of data that can be formatted by this class
     */
    @Override
    public List<String> getSupportedTypes() {
        return List.of("ConcentrationMeanTemplate");
    }

    /**
     * Formats the input string value based on the provided key.
     *
     * @param key   the key indicating the type of formatting to apply
     * @param value the string value to format
     * @return the formatted string value
     */
    @Override
    public String formatStringValue(String key, String value) {
        if (value == null) {
            return null;
        }

        // Elimina espacios, saltos de línea y tabulaciones
        String limpio = value.replaceAll("[\\s\\n\\r\\t]", "");
        // Busca el número decimal (puede adaptarse si usas coma o punto como separador decimal)
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+[.,]?\\d*)").matcher(limpio);
        if (matcher.find()) {
            return matcher.group(1); // Devuelve el número encontrado
        }
        return ""; // Si no encuentra un número
    }
}
