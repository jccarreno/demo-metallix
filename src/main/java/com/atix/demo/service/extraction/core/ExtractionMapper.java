package com.atix.demo.service.extraction.core;

import com.xirius.seiz.core.extraction.fields.FieldResponse;
import com.atix.demo.utils.mapper.Mappeable;

import java.util.List;

/**
 * Interface that performs the mapping of the extracted data.
 *
 * <p>
 * Take in account that the method {@link Mappeable#getSupportedTypes()} is
 * flexible in its
 * formatting, since substrings of the types can be used.
 * For example, if there is a class "EXAMPLE_TYPE_1" and 1
 * {@link ExtractionMapper} for the type "EXAMPLE"
 * then a match will be found.
 *
 * <p>
 * Furthermore, if there are multiple matches then the longest string is chosen.
 * For example, if there is a class "EXAMPLE_TYPE_1" and 2
 * {@link ExtractionMapper ExtractionMappers}, one for the type "EXAMPLE"
 * and another for "EXAMPLE_TYPE" then the <b>second</b>
 * {@link ExtractionMapper} will be returned.
 *
 * <p>
 * If there are multiple matches of the same length there is no guarantee as for
 * which {@link ExtractionMapper}
 * (of the ones that match the criteria) will be used.
 */
public interface ExtractionMapper extends Mappeable<String> {
    /**
     * Maps the extraction response into a properly formatted Object
     *
     * @param fieldResponses the extraction responses list
     * @param templateId     the ID of the template that was used to extract the
     *                       document
     *
     * @return an object containing the extracted data
     */
    ExtractionMapperResult mapExtraction(List<FieldResponse> fieldResponses, String templateId);
}
