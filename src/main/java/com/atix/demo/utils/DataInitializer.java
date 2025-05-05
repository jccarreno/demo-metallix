package com.atix.demo.utils;

import com.atix.demo.domain.DocumentTemplate;
import com.atix.demo.repository.DocumentTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(DocumentTemplateRepository repository) {
        return args -> {
            String defaultId = "ConcentrationMeanTemplate";
            if (!repository.existsById(defaultId)) {
                DocumentTemplate template = new DocumentTemplate();
                template.setId(defaultId);
                template.setTemplatePath("ConcentrationMeanTemplate.json");
                template.setDescription("Plantilla de concentración por defecto");
                repository.save(template);
            }
        };
    }
}
