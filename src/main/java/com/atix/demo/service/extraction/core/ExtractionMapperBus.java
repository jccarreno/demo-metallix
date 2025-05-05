package com.atix.demo.service.extraction.core;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atix.demo.utils.mapper.Mappeable;
import com.atix.demo.utils.mapper.MappeableUtils;

/**
 * Container of {@link ExtractionMapper Extraction Mappers}
 */
@Service
public class ExtractionMapperBus {
    private final Map<String, Mappeable<String>> extractionMappers;

    @Autowired
    public ExtractionMapperBus(List<ExtractionMapper> extractionMappers) {
        this.extractionMappers = new HashMap<>();
        MappeableUtils.doMap(this.extractionMappers, extractionMappers);
    }

    /**
     * Gets the appropiate {@link ExtractionMapper} for a template
     *
     * @param templateId the template ID
     * @return the {@link ExtractionMapper} assigned to the templateId. If there is
     * no appropiate {@link ExtractionMapper} then {@code null} is returned
     */
    public ExtractionMapper getExtractionMapper(String templateId) {
        return (ExtractionMapper) MappeableUtils.getExactOrPartialMatch(this.extractionMappers, templateId);
    }

}
