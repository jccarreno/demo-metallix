package com.atix.demo.service.document;

import com.atix.demo.domain.Document;
import com.atix.demo.service.storage.StorageProvider;
import com.atix.demo.dto.DetectDocResponse;
import com.atix.demo.domain.exceptions.InvalidExtensionException;
import com.atix.demo.dto.DetectUrlRequest;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio encargado de procesar documentos para OCR y otras operaciones relacionadas.
 */
@RequiredArgsConstructor
@Service
public class DetectDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DetectDocumentService.class);

    private static final String FOLDER_ROOT = "detect";
    private static final String SEPARATOR = "/";
    private static final int MAX_WAIT_MINUTES = 3;
    private static final int POLLING_INTERVAL_MS = 4200;
    private static final long MAX_FILE_SIZE_MB = 10L;

    private final DocumentService documentService;
    private final StorageProvider storageProvider;

    @Value("${cloud.buckets.docs}")
    private String bucketDocs;

    @Value("${cloud.buckets.ocr}")
    private String bucketOcr;

    /**
     * Obtiene el OCR para un idFile dado.
     *
     * @param idFile el ID del archivo para el cual se realizará el OCR
     * @return un {@link InputStream} con el resultado del OCR
     * @throws InterruptedException si la operación es interrumpida
     */
    public InputStream getOcrResult(String idFile) throws InterruptedException {
        String path = buildPathForOcr(idFile);

        if (!isDocUploaded(idFile)) {
            throw new RuntimeException("Documento no encontrado");
        }

        List<Blob> blobs = waitForOcrResults(path);

        return new ByteArrayInputStream(blobs.get(0).getContent());
    }

    /**
     * Genera un link firmado para la subida de un archivo
     *
     * @param detectUrlReq contiene información que permite la creación del link firmado
     * @return un {@link DetectDocResponse} que contiene idFile y el link firmado para la subida de archivos.
     */
    public DetectDocResponse generateDetectDoc(DetectUrlRequest detectUrlReq) {
        checkFileExtension(detectUrlReq.getFilename());
        UUID uniqueId = UUID.randomUUID();
        String path = buildPathDocument(uniqueId.toString(), detectUrlReq.getFilename(), false);
        Map<String, String> metadata = buildMetadata(detectUrlReq);
        String signedLink = storageProvider.getUploadSignedUrl(bucketDocs, path, 60, metadata);
        LocalDateTime updateDate = LocalDateTime.now();
        documentService.save(new Document(uniqueId, detectUrlReq.getFilename(), updateDate, path));
        return new DetectDocResponse(uniqueId.toString(), signedLink);
    }

    /**
     * Obtiene el documento original a partir de su ID.
     *
     * @param idFile el ID del archivo del documento
     * @param test indica si es un entorno de prueba
     * @return un {@link InputStream} con el documento original
     * @throws IOException si ocurre un error de entrada/salida
     */
    public InputStream getOriginalDocument(UUID idFile, boolean test) throws IOException {
        Document document = documentService.findByIdFile(idFile);
        String path = buildPathDocument(idFile.toString(), document.getFileName(), test);
        return storageProvider.getObject(bucketDocs, path);
    }

    /**
     * Verifica la extensión de un archivo.
     *
     * @param filename el nombre del archivo a verificar
     */
    private void checkFileExtension(String filename) {
        ContentInfo info = ContentInfoUtil.findExtensionMatch(filename);
        if (info == null) {
            throw new InvalidExtensionException("Formato de archivo desconocido");
        }

        ContentType type = info.getContentType();
        if (type != ContentType.PDF && type != ContentType.MICROSOFT_EXCEL && type != ContentType.MICROSOFT_EXCEL_XML && type!=ContentType.PNG && type!=ContentType.JPEG) {
            throw new InvalidExtensionException("El tipo " + type.getMimeType() + " no es soportado por el sistema");
        }
    }

    /**
     * Construye la ruta del documento basada en el ID único, el nombre del archivo,
     * el tipo de documento y si es de prueba o no.
     *
     * @param uniqueId el ID único del documento
     * @param fileName el nombre del archivo
     * @param type el tipo de documento
     * @param test indica si es un entorno de prueba
     * @return la ruta completa del documento
     */
    private String buildPathDocument(String uniqueId, String fileName, boolean test) {
        String path = FOLDER_ROOT + SEPARATOR + uniqueId + SEPARATOR;
        path += test ? "documento.pdf" : fileName;
        return path;
    }

    /**
     * Construye la ruta para almacenar los resultados del OCR.
     *
     * @param idFile el ID del archivo
     * @return la ruta donde se almacenarán los resultados del OCR
     */
    private String buildPathForOcr(String idFile) {
        return FOLDER_ROOT + SEPARATOR + idFile + SEPARATOR;
    }

    /**
     * Construye los metadatos para la creación de link.
     *
     * @param detectUrlReq la solicitud que contiene datos del documento
     * @return un mapa con los metadatos construidos
     */
    private Map<String, String> buildMetadata(DetectUrlRequest detectUrlReq) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("x-goog-meta-password", detectUrlReq.getPassword());
        metadata.put("x-goog-content-length-range", "0," + (MAX_FILE_SIZE_MB * 1048576)); // 10MiB
        return metadata;
    }

    /**
     * Verifica si un documento ha sido subido.
     *
     * @param idFile el ID del archivo a verificar
     * @return true si el documento ha sido subido, false en caso contrario
     */
    private boolean isDocUploaded(String idFile) {
        String path = buildPathForOcr(idFile);
        Page<Blob> blobs = (Page<Blob>) storageProvider.listFiles(bucketDocs, path);
        return blobs.getValues().iterator().hasNext();
    }

    /**
     * Espera a que los resultados del OCR estén disponibles.
     *
     * @param path la ruta donde se esperan los resultados del OCR
     * @return una lista de {@link Blob} que contiene los resultados del OCR
     * @throws InterruptedException si la operación es interrumpida
     */
    private List<Blob> waitForOcrResults(String path) throws InterruptedException {
        List<Blob> blobs = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.now();

        while (blobs.isEmpty()) {
            blobs = getNonDirectoryBlobs(path);
            if (blobs.isEmpty()) {
                Thread.sleep(POLLING_INTERVAL_MS);
            }
            if (ChronoUnit.MINUTES.between(startTime, LocalDateTime.now()) > MAX_WAIT_MINUTES) {
                throw new RuntimeException("Problema con OCR");
            }
        }

        return blobs.stream()
                .sorted(Comparator.comparing(BlobInfo::getName))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los blobs que no son directorios en una ruta específica.
     *
     * @param path la ruta donde se buscarán los blobs
     * @return una lista de {@link Blob} que no son directorios
     */
    private List<Blob> getNonDirectoryBlobs(String path) {
        Page<Blob> blobPage = (Page<Blob>) storageProvider.listFiles(bucketOcr, path);
        List<Blob> blobs = new ArrayList<>();
        for (Blob blob : blobPage.getValues()) {
            if (!blob.isDirectory()) {
                blobs.add(blob);
            }
        }
        return blobs;
    }

    public String generateDownloadUrl(String path) {
        return storageProvider.getDownloadSignedUrl(bucketDocs, path, 60);
    }

}
