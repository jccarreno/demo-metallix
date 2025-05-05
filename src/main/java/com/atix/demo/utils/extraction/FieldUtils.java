package com.atix.demo.utils.extraction;


import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import com.xirius.seiz.core.extraction.fields.FieldResponse;

/**
 * Class to deal with fields of Seiz Extraction
 */
public class FieldUtils {

    /**
     * Gets a cell from a list of field responses. This is based in the structure of
     * the subFieldId created by the TableFieldExtractor. You should first filter
     * the responses by their fieldName, so that their subFieldId are unique.
     * Otherwise, the returned cell may be different from the expected result.
     *
     * @param responses the list of responses
     * @param row       the row that is desired to be obtained
     * @param col       the col that is desired to be obtained
     * @return the FieldResponse corresponding to the row and column
     */
    public static FieldResponse getTableElement(List<FieldResponse> responses, int row, int col) {
        List<FieldResponse> cell = filterBySubFieldId(responses, row + ":" + col);
        return (cell == null || cell.isEmpty()) ? null : cell.get(0);
    }

    /**
     * Groups the responses by page
     *
     * @param responses the list of responses
     * @return a Map of the responses, where the keys are the pages where the fields
     * where extracted from
     */
    public static Map<Integer, List<FieldResponse>> groupByPage(List<FieldResponse> responses) {
        return responses.stream().collect(Collectors.groupingBy(FieldResponse::getPageNumber));
    }

    /**
     * Filters the field responses by their field name
     *
     * @param responses the list of responses
     * @param regex     the regular expression that will be used for the search
     * @return the filtered list of responses
     */
    public static List<FieldResponse> filterByFieldname(List<FieldResponse> responses, String regex) {
        return responses.stream().filter(x -> x.getFieldName().matches(regex)).collect(Collectors.toList());
    }

    /**
     * Filters the field responses by their subfield ID
     *
     * @param responses the list of responses
     * @param regex     the regular expression that will be used for the search
     * @return the filtered list of responses
     */
    public static List<FieldResponse> filterBySubFieldId(List<FieldResponse> responses, String regex) {
        return responses.stream().filter(x -> x.getSubFieldId() != null && x.getSubFieldId().matches(regex))
                .collect(Collectors.toList());
    }

    /**
     * Filters the field responses so that only non-complex fields are returned. For
     * this method, a complex field is one that can return multiple values for the
     * same key.
     *
     * @param responses the list of responses*
     * @return the filtered list of responses
     */
    public static List<FieldResponse> filterBySimpleFields(List<FieldResponse> responses) {
        return responses.stream().filter(x -> x.getSubFieldId() == null)
                .collect(Collectors.toList());
    }

    /**
     * Creates a map of the responses, where the key of each field is used as the
     * key for the map
     *
     * @param responses the list of responses
     * @return a Map of the field responses
     */
    public static Map<String, FieldResponse> toMap(List<FieldResponse> responses) {
        return responses.stream()
                .collect(Collectors.toMap(FieldResponse::getFieldName, x -> x));
    }

    /**
     * Creates a map of the responses, where the key of each field is used as the
     * key for the map
     *
     * @param responses the list of responses
     * @param merger    the merge function used when there are conflicts. You can use
     *                  {@link FieldUtils#defaultMergeResponses(FieldResponse, FieldResponse)}
     *                  for most cases
     * @return a Map of the field responses
     */
    public static Map<String, FieldResponse> toMap(List<FieldResponse> responses, BinaryOperator<FieldResponse> merger) {
        return responses.stream()
                .collect(Collectors.toMap(FieldResponse::getFieldName, x -> x, merger));
    }

    /**
     * Fuses the text of two responses using {@param f1} as basis. Other fields are not merged
     *
     * @param f1 the first response
     * @param f2 the second response
     * @return a fused response
     */
    public static FieldResponse defaultMergeResponses(FieldResponse f1, FieldResponse f2) {
        f1.setText(f1.getText().trim() + "\n" + f2.getText().trim());
        return f1;
    }

    /**
     * Creates a map of the responses, where the key of each field is used as the
     * key for the map, duplicate keys do not generate error
     *
     * @param responses the list of responses
     * @return a Map of the field responses
     */
    public static Map<String, FieldResponse> toMapIgnoreDuplicates(List<FieldResponse> responses) {
        return responses.stream()
                .collect(Collectors.toMap(FieldResponse::getFieldName, x -> x, (a1, a2) -> a1));
    }
}
