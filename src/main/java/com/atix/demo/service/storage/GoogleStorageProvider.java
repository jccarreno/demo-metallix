package com.atix.demo.service.storage;

import com.google.cloud.storage.*;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

@Service
public class GoogleStorageProvider implements StorageProvider {

    private final Storage googleStorage;

    public GoogleStorageProvider() {
        googleStorage = StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public Object putObject(String bucket, String path, InputStream content, String contentType) {
        return googleStorage.get(bucket).create(path, content, contentType);
    }

    @Override
    public InputStream getObject(String bucket, String path) {
        Blob blob = googleStorage.get(BlobId.of(bucket, path));
        if (blob != null) {
            return new ByteArrayInputStream(blob.getContent(Blob.BlobSourceOption.generationMatch()));
        }
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public String getDownloadSignedUrl(String bucket, String path, long minutes) {
        if (minutes < 0)
            throw new IllegalArgumentException("La fecha de expiración no puede ser anterior a la fecha actual");
        return googleStorage.signUrl(BlobInfo.newBuilder(bucket, path).build(), minutes, TimeUnit.MINUTES).toString();
    }

    @Override
    public String getUploadSignedUrl(String bucket, String path, long minutes, Map<String, String> metadata) {
        if (minutes < 0)
            throw new IllegalArgumentException("La fecha de expiración no puede ser anterior a la fecha actual");
        Map<String, String> extensionHeaders = new HashMap<>();
        if (metadata != null) {
            for (Entry<String, String> entry : metadata.entrySet()) {
                if (entry.getValue() != null)
                    extensionHeaders.put(entry.getKey(), entry.getValue());
            }
        }
        ContentInfo info = ContentInfoUtil.findExtensionMatch(path);
        extensionHeaders.put("Content-Type", info.getMimeType());
        return googleStorage
                .signUrl(BlobInfo.newBuilder(bucket, path).build(),
                        minutes, TimeUnit.MINUTES, Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                        Storage.SignUrlOption.withExtHeaders(extensionHeaders),
                        Storage.SignUrlOption.withV4Signature())
                .toString();
    }

    @Override
    public Object listFiles(String bucket, String directoryPrefix) {
        return googleStorage.list(bucket, Storage.BlobListOption.prefix(directoryPrefix),
                Storage.BlobListOption.currentDirectory());
    }

    @Override
    public boolean deleteObject(String bucket, String path) {
        return googleStorage.delete(BlobId.of(bucket, path));
    }

    @Override
    public void moveObject(String fromBucket, String fromPath, String toBucket, String toPath) {
        Blob blob = googleStorage.get(BlobId.of(fromBucket, fromPath));
        blob.copyTo(toBucket, toPath);
        blob.delete();
    }

    @Override
    public boolean exists(String bucket, String path) {
        return googleStorage.get(BlobId.of(bucket, path)) != null;
    }

}
