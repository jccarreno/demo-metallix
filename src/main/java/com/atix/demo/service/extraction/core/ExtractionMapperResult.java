package com.atix.demo.service.extraction.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Contiene el resultado del mapeo de la extracción
 */
@AllArgsConstructor
@Getter
@Setter
public class ExtractionMapperResult {
    /**
     * The extracted data that should be returned by the system
     */
    private Object extractedData;
    /**
     * Data that is needed by the application to perform validations or other operations,
     * but that should not be returned to an external system
     */
    private Object internalExtractedData;
}
