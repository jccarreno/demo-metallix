package com.atix.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * La clase Document representa un documento subido al sistema, que puede incluir
 * información relacionada con el nombre del archivo, su tipo, su ubicación en el sistema de archivos,
 * y su estado.
 */
@Entity
@Table(name = "Document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    /**
     * Identificador único para cada documento.
     * Se genera automáticamente mediante una estrategia de incremento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador único del archivo (UUID).
     * Este campo es obligatorio y se almacena como BINARY(16) en la base de datos.
     */
    @Column(name = "id_file", nullable = false, columnDefinition = "BINARY(16)")
    private UUID idFile;

    /**
     * Nombre del archivo que se ha subido.
     */
    @Column(name = "file_name")
    private String fileName;


    /**
     * Fecha en la que el archivo fue subido.
     * Este campo es obligatorio y no puede ser actualizado después de la creación.
     */
    @Column(name = "upload_date", nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    /**
     * Ruta del archivo en el sistema o en el almacenamiento (por ejemplo, ruta en un bucket de GCP).
     * El tamaño máximo es de 500 caracteres.
     */
    @Column(name = "file_path", length = 500)
    private String filePath;

    /**
     * Mensaje o descripción asociado al documento, generalmente para información adicional sobre el estado o contenido del archivo.
     */
    @Column(name = "message")
    private String message;

    /**
     * Estado del documento, representado como un valor entero.
     * Por defecto, el estado es 200, lo que podría indicar éxito o carga completada.
     */
    @Column(name = "status")
    private Integer status = 200;

    /**
     * Constructor adicional que permite inicializar los campos `idFile`, `fileName`, `uploadDate`, `filePath` y `documentType`.
     * @param idFile UUID del archivo.
     * @param fileName Nombre del archivo.
     * @param uploadDate Fecha en que se subió el archivo.
     * @param path Ruta en el sistema o almacenamiento.
     * @param type Tipo de documento (enum).
     */
    public Document(UUID idFile, String fileName, LocalDateTime uploadDate, String path) {
        this.idFile = idFile;
        this.fileName = fileName;
        this.uploadDate = uploadDate;
        this.filePath = path;
    }
}
