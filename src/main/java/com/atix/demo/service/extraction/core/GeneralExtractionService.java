package com.atix.demo.service.extraction.core;


import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.atix.demo.domain.exceptions.DuplicateCreditNoteException;
import com.atix.demo.dto.ExtResponseDTO;
import com.atix.demo.service.ConcentrationMeanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.atix.demo.domain.*;
import com.atix.demo.service.document.*;
import com.atix.demo.dto.ConcentrationMeanDTO;
import com.atix.demo.dto.ExtRequestDTO;
import com.atix.demo.utils.image.ImageUtils;

import com.xirius.seiz.core.commons.model.text.DocumentSeiz;
import com.xirius.seiz.core.commons.model.ImageSeiz;
import com.xirius.seiz.core.extraction.model.DocumentExtractionTemplate;
import com.xirius.seiz.core.commons.files.InputStreamFile;
import com.xirius.seiz.core.commons.files.TransferFile;
import com.xirius.seiz.plugins.commons.opencv.OpenCVUtils;
import com.xirius.seiz.core.extraction.fields.*;

/**
 * Service responsible for the extraction and formatting of data, including handling documents
 * and managing the retrieval of necessary information based on different extraction templates.
 */
@RequiredArgsConstructor
@Service
public class GeneralExtractionService {

    private final ExtractionMapperBus extractionMapperBus;
    private final DocumentService documentService;
    private final Gson gson = new Gson();
    private final ConcentrationMeanService concentrationMeanService;
    private final TemplateService templateService;
    private final FieldExtractionService fieldExtractionService = new FieldExtractionService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DetectDocumentService detectDocumentService;
    private final Logger logger = LoggerFactory.getLogger(GeneralExtractionService.class);


    /**
     * Retrieves a document based on the given request.
     *
     * @param request the extraction request containing the document information
     * @return the {@link Document} retrieved from the request
     */
    private Document getDocument(ExtRequestDTO request) {
        return documentService.findByIdFile(request.getIdFile());
    }

    /**
     * Creates a {@link CreditNoteDTO} based on the provided data.
     *
     * @param data the extracted data to be formatted into a CreditNoteDTO
     * @return the created {@link CreditNoteDTO}
     */
    private ConcentrationMeanDTO createConcentrationMeanDTO(Object data) {
        Map<String, Object> extractedData = objectMapper.convertValue(data, Map.class);
        return ConcentrationMeanDTO.builder()
                .Ag(convertToBigDecimal(extractedData.get("Ag")))
                .Au(convertToBigDecimal(extractedData.get("Au")))
                .Cu(convertToBigDecimal(extractedData.get("Cu")))
                .Ir(convertToBigDecimal(extractedData.get("Ir")))
                .Pd(convertToBigDecimal(extractedData.get("Pd")))
                .Pt(convertToBigDecimal(extractedData.get("Pt")))
                .Rh(convertToBigDecimal(extractedData.get("Rh")))
                .Ru(convertToBigDecimal(extractedData.get("Ru")))
                .build();
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

    /**
     * Extracts data of any type based on the provided extraction request.
     *
     * @param extractionReq the extraction request containing data
     * @param test          whether the extraction is being performed in a test environment
     * @return a pair consisting of {@link FieldExtractionResponse} and a String identifier
     */
    private Pair<FieldExtractionResponse, String> extractAnyType(ExtRequestDTO extractionReq, boolean test)
            throws IOException, InterruptedException {

        String classId = extractionReq.getIdTemplate();
        logger.info(" Extraction of [{}], idFile: [{}]", classId, extractionReq.getIdFile());
        InputStream ocr = detectDocumentService.getOcrResult(extractionReq.getIdFile().toString());
        DocumentSeiz documentSeiz = gson.fromJson(IOUtils.toString(ocr, StandardCharsets.UTF_8),
                DocumentSeiz.class);
        logger.info(" Class: [{}], idFile: [{}]", classId, extractionReq.getIdFile());
        Pair<String, DocumentExtractionTemplate> template = templateService.getTemplate(classId);
        String templateId = template.getFirst();
        logger.info(" Template ID: [{}], idFile: [{}]", templateId, extractionReq.getIdFile());
        List<ImageSeiz> images = maybeGetImages(template.getSecond(), extractionReq.getIdFile().toString(), test);
        return Pair.of(getExtResponse(template.getSecond(), images, documentSeiz), templateId);
    }

    /**
     * Extracts and formats the data of a single document.
     *
     * @param request the request with the data of the document.
     * @param test    indicates that the extraction module is in testing mode.
     * @return the response with the extracted data. The first part of the pair contains
     * the document data and the second contains the extracted data.
     */
    public Pair<ConcentrationMeanDTO, ExtResponseDTO> extractAndFormat(ExtRequestDTO request, boolean test) {
        ConcentrationMeanDTO concentrationMeanDTO = null;
        try {
            Pair<FieldExtractionResponse, String> fieldExtractionResponse = extractAnyType(request, test);
            ExtractionMapper extractionMapper = extractionMapperBus.getExtractionMapper(fieldExtractionResponse.getSecond());
            ExtractionMapperResult extractionResult = extractionMapper.mapExtraction(
                    fieldExtractionResponse.getFirst().getFieldResponses(), fieldExtractionResponse.getSecond());
            Object data = extractionResult.getExtractedData();
            Document document = getDocument(request);
            concentrationMeanDTO = createConcentrationMeanDTO(data);

            if (!test) {
                concentrationMeanService.save(concentrationMeanDTO,document);
            }
            // Generar el signedUrl
            String signedUrl = detectDocumentService.generateDownloadUrl(document.getFilePath());
            // Crear el DTO de respuesta
            InputStream ocrResult=detectDocumentService.getOcrResult(request.getIdFile().toString());
            ExtResponseDTO responseDTO = new ExtResponseDTO(request.getIdFile(), data, signedUrl, concentrationMeanService.getAverageConfidence(ocrResult), concentrationMeanService.getMinConfidence(ocrResult), concentrationMeanService.getMaxConfidence(ocrResult));

            return Pair.of(concentrationMeanDTO, responseDTO);
        } catch (DuplicateCreditNoteException e) {
            logger.error("Error al guardar la nota de crédito: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing extraction request", e);
        }
    }

    /**
     * Retrieves the {@link DocumentSeiz} associated with the given file ID.
     *
     * @param idFile the ID of the file associated with the document
     * @return the retrieved {@link DocumentSeiz}
     */
    private DocumentSeiz getDocumentSeiz(String idFile) throws IOException, InterruptedException {
        InputStream ocr = detectDocumentService.getOcrResult(idFile);
        return gson.fromJson(IOUtils.toString(ocr, StandardCharsets.UTF_8), DocumentSeiz.class);
    }

    /**
     * Retrieves a list of imageSeiz associated with the document, if required by the extraction template.
     *
     * @param template the extraction template defining the requirements
     * @param idFile   the ID of the file
     * @param test     whether the extraction is being performed in a test environment
     * @return a list of {@link ImageSeiz} objects, if applicable
     */
    private List<ImageSeiz> maybeGetImages(DocumentExtractionTemplate template, String idFile, boolean test) {
        if (requiresImages(template)) {
            try {
                TransferFile file = new InputStreamFile(idFile + ".pdf", detectDocumentService.getOriginalDocument(UUID.fromString(idFile), test));
                return OpenCVUtils.transferFileToBufferedImages(file, 250).stream()
                        .map(image -> ImageSeiz.Builder.builder()
                                .format(ImageUtils.getImageFormat(file.getName()))
                                .originalImage(image)
                                .build())
                        .collect(Collectors.toList());
            } catch (IOException e) {
                logger.error("Error getting images for file [{}]", idFile, e);
            }
        }
        return Collections.emptyList();
    }


    /**
     * Determines whether the provided extraction template requires images for the extraction process.
     *
     * @param template the extraction template defining the requirements
     * @return true if images are required, false otherwise
     */
    private boolean requiresImages(DocumentExtractionTemplate template) {
        return template.getRequests().stream()
                .flatMap(request -> request.getRequestedFields().stream())
                .anyMatch(field -> doesFieldRequireImages(field.getType()));
    }

    /**
     * Generates an extraction response based on the provided template document.
     *
     * @param template     the extraction template defining the response structure
     * @param images       the list of images to be included in the response
     * @param documentSeiz the document associated with the extraction
     * @return the {@link FieldExtractionResponse} containing the extracted data
     */
    private FieldExtractionResponse getExtResponse(DocumentExtractionTemplate template, List<ImageSeiz> images,
                                                   DocumentSeiz documentSeiz) {
        DocumentExtractionRequest documentExtractionRequest = new DocumentExtractionRequest();
        documentExtractionRequest.setTemplate(template);
        documentExtractionRequest.setDocument(documentSeiz);
        documentExtractionRequest.setImages(images);

        FieldExtractionResponse fieldExtractionResponse = new FieldExtractionResponse();
        fieldExtractionResponse.setFieldResponses(fieldExtractionService.extractDocument(documentExtractionRequest));
        return fieldExtractionResponse;
    }

    /**
     * Determines whether a specific field type requires images for extraction.
     *
     * @param fieldType the type of the field to check
     * @return true if the field type requires images, false otherwise
     */
    private boolean doesFieldRequireImages(String fieldType) {
        return Arrays.asList("TABLE", "BARCODE").contains(fieldType);
    }
}