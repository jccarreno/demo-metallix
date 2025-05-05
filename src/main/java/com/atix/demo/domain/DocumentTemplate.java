package com.atix.demo.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

/**
 * Representa una plantilla para un documento en el sistema.
 * Esta entidad se utiliza para almacenar información relacionada con plantillas de documentos,
 * como el usuario propietario, la ruta de la plantilla y una descripción opcional.
 */
@Setter
@Getter
@Entity
public class DocumentTemplate {
    
    @Id
    private String id="ConcentrationMeanTemplate";

    /**
     * Ruta del archivo donde se almacena la plantilla del documento.
     * Este campo es obligatorio y no puede ser nulo.
     */
    @Column(name = "template_path", nullable = false)
    private String templatePath="ConcentrationMeanTemplate.json";

    /**
     * Descripción opcional de la plantilla del documento.
     * Puede contener información adicional sobre la finalidad o el uso de la plantilla.
     */
    @Column(name = "description")
    private String description;
}
