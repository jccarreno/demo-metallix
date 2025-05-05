package com.atix.demo.service.extraction.core;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atix.demo.utils.mapper.Mappeable;
import com.atix.demo.utils.mapper.MappeableUtils;

/**
 * Container of {@link Formatter Formatters}
 */
@Service
public class FormatterBus {
    private final Map<String, Mappeable<String>> formatters;

    @Autowired
    public FormatterBus(List<Formatter> formatters) {
        this.formatters = new HashMap<>();
        MappeableUtils.doMap(this.formatters, formatters);
    }

    /**
     * Gets the appropiate {@link Formatter} for a template
     *
     * @param templateId the template ID
     * @return the {@link Formatter} assigned to the templateId. If there is no
     *         appropiate {@link Formatter} then {@code null} is returned
     */
    public Formatter getFormatter(String templateId) {
        return (Formatter) MappeableUtils.getExactOrPartialMatch(this.formatters, templateId);
    }

}