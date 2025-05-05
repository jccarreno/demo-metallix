package com.atix.demo.service.extraction.core;


import java.util.Map;

import com.xirius.seiz.core.extraction.fields.FieldResponse;
import com.xirius.seiz.plugins.commons.utils.StringUtils;

import com.atix.demo.utils.mapper.Mappeable;

/**
 * Interface that performs the cleaning of the extracted data.
 *
 * <p>
 * Take in account that the method {@link Mappeable#getSupportedTypes()} is
 * flexible in its
 * formatting, since substrings of the types can be used.
 * For example, if there is a class "EXAMPLE_TYPE_1" and 1 {@link Formatter} for
 * the type "EXAMPLE"
 * then a match will be found.
 *
 * <p>
 * Furthermore, if there are multiple matches then the longest string is chosen.
 * For example, if there is a class "EXAMPLE_TYPE_1" and 2 {@link Formatter
 * Formatters}, one for the type "EXAMPLE"
 * and another for "EXAMPLE_TYPE" then the <b>second</b> {@link Formatter} will
 * be returned.
 *
 * <p>
 * If there are multiple matches of the same length there is no guarantee as for
 * which {@link Formatter}
 * (of the ones that match the criteria) will be used.
 */
public interface Formatter extends Mappeable<String> {
    /**
     * Cleans and obtains a string value for further processing
     *
     * @param responseMap a map that contains the response from the extraction
     *                    process. This map should ONLY contain simple fields, i.e.
     *                    those with exactly one ocurrence
     * @param key         the key or name of the field
     * @return the formatted extracted value
     */
    default String getStringValue(Map<String, FieldResponse> responseMap, String key) {
        String text = responseMap.getOrDefault(key, new FieldResponse()).getText();
        return formatStringValue(key, StringUtils.isBlank(text) ? "" : text).trim();
    }

    /**
     * Cleans a String value for further processing
     *
     * @param key   the key or name of the field
     * @param value the value of the field
     * @return the formatted value
     */
    String formatStringValue(String key, String value);

    default String getCleanStringValue(Map<String, FieldResponse> responseMap, String key) {
        String text = responseMap.getOrDefault(key, new FieldResponse()).getText();
        return text.replaceAll("[\\s\\n\\r\\t]", "");
    }
}