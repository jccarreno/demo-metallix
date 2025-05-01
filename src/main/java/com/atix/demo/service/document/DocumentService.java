package com.atix.demo.service.document;

import java.util.UUID;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.atix.demo.domain.Document;
import com.atix.demo.domain.exceptions.DocumentNotFoundException;
import com.atix.demo.repository.DocumentRepository;

/**
 * Servicio encargado de la gestión de documentos,
 */
@RequiredArgsConstructor
@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    /**
     * Guarda un documento en la base de datos.
     *
     * @param entity el objeto {@link Document} que se desea guardar
     * @return el objeto {@link Document} guardado
     */
    @Transactional
    public Document save(Document entity) {
        return documentRepository.save(entity);
    }

    /**
     * Registra los errores ocurridos durante el procesamiento de un archivo.
     *
     * @param idFile el UUID del archivo que presentó errores
     * @param status el estado del error ocurrido
     * @param message el mensaje descriptivo del error
     * @return el objeto {@link Document} actualizado con los errores registrados
     */
    @Transactional
    public Document registerErrors(UUID idFile, int status, String message) {
        Document doc = this.findByIdFile(idFile);
        doc.setMessage(message);
        doc.setStatus(status);
        return this.save(doc);
    }

    /**
     * Finds a document by its UUID
     *
     * @param idFile the UUID of the file
     * @return the document with the provided UUID
     * @throws DocumentNotFoundException if the document is not found
     */
    public Document findByIdFile(UUID idFile) throws DocumentNotFoundException {
        Document doc = documentRepository.findByIdFile(idFile);
        if (doc == null)
            throw new DocumentNotFoundException(
                    "El documento " + idFile.toString() + " no está registrado en el sistema");
        return doc;
    }

}
