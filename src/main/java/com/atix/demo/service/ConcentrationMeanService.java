package com.atix.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atix.demo.domain.ConcentrationMean;
import com.atix.demo.domain.Document;
import com.atix.demo.dto.ConcentrationMeanDTO;
import com.atix.demo.repository.ConcentrationMeanRepository;
import com.atix.demo.service.document.DetectDocumentService;
import com.atix.demo.service.document.DocumentService;
import com.atix.demo.utils.mapper.ConcentrationMeanMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ConcentrationMeanService {
    private final ConcentrationMeanRepository concentrationMeanRepository;
    private final DocumentService documentService;
    private final ConcentrationMeanMapper mapper;
    private final DetectDocumentService detectDocumentService;

    @Transactional
    public void save(ConcentrationMeanDTO creditNoteDTO, Document document) {
        ConcentrationMean creditNote = mapper.toEntity(creditNoteDTO);
        creditNote.setDocument(document);

        save(creditNote);
    }

    public ConcentrationMean save(ConcentrationMean concentrationMean)
    {
        return concentrationMeanRepository.save(concentrationMean);
    }

    public double getAverageConfidence(InputStream inputStream) throws IOException {
        List<Double> confidenceValues = extractConfidenceValues(inputStream);
        
        OptionalDouble average = confidenceValues.stream()
                .mapToDouble(Double::doubleValue)
                .average();
        
        return average.orElse(0.0);
    }

    /**
     * Encuentra el valor mínimo de "confidence" en el archivo JSON OCR.
     * 
     * @param inputStream Stream del archivo JSON OCR
     * @return El valor mínimo de "confidence" encontrado
     * @throws IOException Si hay un error al leer o procesar el JSON
     */
    public double getMinConfidence(InputStream inputStream) throws IOException {
        List<Double> confidenceValues = extractConfidenceValues(inputStream);
        
        return confidenceValues.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
    }

    /**
     * Encuentra el valor máximo de "confidence" en el archivo JSON OCR.
     * 
     * @param inputStream Stream del archivo JSON OCR
     * @return El valor máximo de "confidence" encontrado
     * @throws IOException Si hay un error al leer o procesar el JSON
     */
    public double getMaxConfidence(InputStream inputStream) throws IOException {
        List<Double> confidenceValues = extractConfidenceValues(inputStream);
        
        return confidenceValues.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
    }

    /**
     * Método auxiliar para extraer todos los valores de "confidence" del JSON OCR.
     * 
     * @param inputStream Stream del archivo JSON OCR
     * @return Lista con todos los valores de "confidence" encontrados
     * @throws IOException Si hay un error al leer o procesar el JSON
     */
    private List<Double> extractConfidenceValues(InputStream inputStream) throws IOException {
        List<Double> confidenceValues = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        
        // Reiniciamos el InputStream para asegurarnos de leerlo desde el principio
        inputStream.reset();
        
        // Parseamos el JSON
        JsonNode rootNode = mapper.readTree(inputStream);
        JsonNode pagesNode = rootNode.path("pages");
        
        // Recorremos todas las páginas
        for (JsonNode pageNode : pagesNode) {
            // Obtenemos la confianza general de la página
            if (pageNode.has("confidence")) {
                confidenceValues.add(pageNode.path("confidence").asDouble());
            }
            
            // Recorremos todas las palabras de la página
            JsonNode wordsNode = pageNode.path("words");
            for (JsonNode wordNode : wordsNode) {
                if (wordNode.has("confidence")) {
                    confidenceValues.add(wordNode.path("confidence").asDouble());
                }
            }
        }
        
        return confidenceValues;
    }
}
