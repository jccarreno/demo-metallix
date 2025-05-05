package com.atix.demo.service.extraction.impl;

import com.atix.demo.service.extraction.core.ExtractionMapper;

import com.atix.demo.dto.ConcentrationMeanDTO;
import com.xirius.seiz.core.extraction.fields.FieldResponse;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;
import com.atix.demo.utils.extraction.FieldUtils;

import com.atix.demo.service.extraction.core.Formatter;
import com.atix.demo.service.extraction.core.FormatterBus;
import com.atix.demo.service.extraction.core.ExtractionMapperResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper responsible for handling the extraction of data related to credit notes.
 * This class is annotated with {@code @Component} to indicate that it is a Spring component,
 * and {@code @AllArgsConstructor} generates a constructor with all required dependencies.
 */
@AllArgsConstructor
@Component
public class ConcentrationMeanExtractionMapper implements ExtractionMapper {

    private final FormatterBus formatterBus;

    /**
     * Retrieves a list of supported types for extraction mapping .
     *
     * @return a list of {@link String} representing the types of documents supported by this mapper
     */
    @Override
    public List<String> getSupportedTypes() {
        // Devuelve los tipos soportados por este mapper
        return List.of("ConcentrationMeanTemplate");
    }

    /**
     * Maps the extracted field responses to the appropriate format based on the provided template ID.
     *
     * @param fieldResponses a list of {@link FieldResponse} containing the extracted fields
     * @param templateId the ID of the template used for mapping
     * @return an {@link ExtractionMapperResult} containing the result of the mapping process
     */
    @Override
    public ExtractionMapperResult mapExtraction(List<FieldResponse> fieldResponses, String templateId) {
        Formatter formatter = formatterBus.getFormatter(templateId);

        // Validación para asegurarse de que el formatter no sea nulo
        if (formatter == null) {
            throw new IllegalArgumentException("Formatter not found for templateId: " + templateId);
        }

        // Filtra los campos simples y convierte a un mapa para un acceso eficiente
        List<FieldResponse> filteredResponses = FieldUtils.filterBySimpleFields(fieldResponses);
        Map<String, FieldResponse> responseMap = filteredResponses.stream()
                .collect(Collectors.toMap(FieldResponse::getFieldName, response -> response));

        // Mapea las respuestas a un DTO
        ConcentrationMeanDTO data = new ConcentrationMeanDTO();
        data.setMeasuringTime(formatter.getCleanStringValue(responseMap, FieldNamesConcentrationMean.MEASURING_TIME));
        data.setLotNumber(formatter.getCleanStringValue(responseMap, FieldNamesConcentrationMean.LOT_NUMBER));
        data.setWorkNumber(formatter.getCleanStringValue(responseMap, FieldNamesConcentrationMean.WORK_NUMBER));
        data.setDate(formatter.getCleanStringValue(responseMap, FieldNamesConcentrationMean.DATE));
        data.setTime(formatter.getCleanStringValue(responseMap, FieldNamesConcentrationMean.TIME));
        data.setAg(convertToBigDecimal(formatter.getStringValue(responseMap, FieldNamesConcentrationMean.AG)));
        data.setAu(convertToBigDecimal(formatter.getStringValue(responseMap, FieldNamesConcentrationMean.AU)));
        data.setCu(convertToBigDecimal(formatter.getStringValue(responseMap, FieldNamesConcentrationMean.CU)));
        data.setIr(convertToBigDecimal(formatter.getStringValue(responseMap, FieldNamesConcentrationMean.IR)));
        data.setPd(convertToBigDecimal(formatter.getStringValue(responseMap, FieldNamesConcentrationMean.PD)));
        data.setPt(convertToBigDecimal(formatter.getStringValue(responseMap, FieldNamesConcentrationMean.PT)));
        data.setRh(convertToBigDecimal(formatter.getStringValue(responseMap, FieldNamesConcentrationMean.RH)));
        data.setRu(convertToBigDecimal(formatter.getStringValue(responseMap, FieldNamesConcentrationMean.RU)));

        // Retorna el resultado de la extracción
        return new ExtractionMapperResult(data, null);
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        } else if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("No se puede convertir a BigDecimal: " + value, e);
            }
        } else {
            throw new IllegalArgumentException("Tipo no soportado para conversión a BigDecimal: " + value.getClass());
        }
    }
}
