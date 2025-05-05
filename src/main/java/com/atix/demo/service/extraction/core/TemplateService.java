package com.atix.demo.service.extraction.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.atix.demo.repository.DocumentTemplateRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xirius.seiz.core.commons.model.ROI;
import com.xirius.seiz.core.extraction.model.DocumentExtractionTemplate;
import com.atix.demo.domain.DocumentTemplate;
import org.apache.commons.io.IOUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.atix.demo.domain.exceptions.TemplateNotFoundException;
import com.atix.demo.utils.extraction.InterfaceAdapter;

/**
 * Service in charge of template management.
 * Adapted to retrieve templates from local filesystem instead of Google Cloud Storage.
 */
@RequiredArgsConstructor
@Service
public class TemplateService {
    private String TEMPLATES_DIRECTORY="C:/Users/juanc/Desktop/Atix/Demo metallix/demo-metallix/src/main/resources";
    
    private final DocumentTemplateRepository documentTemplateRepository;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(ROI.class, new InterfaceAdapter<ROI>()).create();

    /**
     * Retrieves a template from its class identifier
     *
     * @param classId the identifier of the class associated to the template as
     *                obtained during the classification process
     * @return the extraction template and its identifier
     * @throws IOException if there is an I/O error
     */
    public Pair<String, DocumentExtractionTemplate> getTemplate(String classId) throws IOException {
        Optional<DocumentTemplate> templateBd = documentTemplateRepository.findById(classId);
        if (templateBd == null)
            throw new TemplateNotFoundException("Plantilla no identificada para la clase [" + classId + "]");
        
        // Construir la ruta al archivo de plantilla en el sistema de archivos local
        String templatePath = TEMPLATES_DIRECTORY + "/" + templateBd.get().getTemplatePath();
        File templateFile = new File(templatePath);
        
        // Verificar si el archivo existe
        if (!templateFile.exists() || !templateFile.isFile()) {
            throw new IOException("El archivo de plantilla no existe en la ruta: " + templatePath);
        }
        
        // Leer el archivo del sistema local
        try (InputStream templateStream = new FileInputStream(templateFile)) {
            String templateAsJson = IOUtils.toString(templateStream, StandardCharsets.UTF_8);
            return Pair.of(templateBd.get().getTemplatePath().replaceFirst(".json", ""),
                    gson.fromJson(templateAsJson, DocumentExtractionTemplate.class));
        }
    }
}