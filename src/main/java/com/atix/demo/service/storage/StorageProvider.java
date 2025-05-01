package com.atix.demo.service.storage;

import java.io.InputStream;
import java.util.Map;

/**
 * StorageProvider defines the contract for interacting with a storage system.
 */
public interface StorageProvider {

    /**
     * Puts an object in the corresponding path
     *
     * @param bucket      the Cloud bucket
     * @param path        the path relative to the bucket where the object will be
     *                    placed
     * @param content     the content to be placed
     * @param contentType the MIME type
     * @return an object that the Cloud Storage service provides to interact with
     * the object that was saved
     */
    Object putObject(String bucket, String path, InputStream content, String contentType);

    /**
     * Determines if an object exists in a given path
     *
     * @param bucket the Cloud bucket
     * @param path   the path relative to the bucket to be checked
     * @return {@code true} if there is an object that corresponds to the path
     */
    boolean exists(String bucket, String path);

    /**
     * Retrieves an object from the Cloud Storage
     *
     * @param bucket the Cloud bucket
     * @param path   the path relative to the bucket to be retrieved
     * @return an {@link InputStream} with the contents of the object
     */
    InputStream getObject(String bucket, String path);

    /**
     * Creates a signed link for uploading an object
     *
     * @param bucket   the bucket
     * @param path     the path relative to the bucket where the object will be
     *                 uploaded
     * @param minutes  number of minutes that the link should be active
     * @param metadata metadata that may be required by a cloud storage vendor. The
     *                 keys of the
     *                 map should be the names of the metadata variables
     * @return the signed URL. An object should be able to be uploaded by using a
     * PUT or POST request to the signed URL (the HTTP method may vary due to
     * the implementation or vendor that is being used)
     */
    String getUploadSignedUrl(String bucket, String path, long minutes, Map<String, String> metadata);

    /**
     * Lists files in a specific directory within the given bucket.
     *
     * @param bucket          The name of the storage bucket.
     * @param directoryPrefix The prefix of the directory to list files from.
     * @return An object representing the list of files in the specified directory.
     */
    Object listFiles(String bucket, String directoryPrefix);

    /**
     * Creates a signed link for downloading an object
     *
     * @param bucket  the bucket
     * @param path    the path relative to the bucket where the object to be
     *                downloaded is located
     * @param minutes number of minutes that the link should be active
     * @return the signed URL. The object should be able to be downloaded by using a
     * GET request to the signed URL
     */
    String getDownloadSignedUrl(String bucket, String path, long minutes);

    /**
     * Deletes an object from a bucket
     *
     * @param bucket the bucket
     * @param path   the path relative to the bucket where the object to be deleted
     *               is located
     * @return {@code true} if the object was successfully deleted
     */
    boolean deleteObject(String bucket, String path);

    /**
     * Moves an object from one location to another
     *
     * @param fromBucket the original bucket
     * @param fromPath   the original path in the bucket
     * @param toBucket   the destiny bucket
     * @param toPath     the destiny path in the bucket
     */
    void moveObject(String fromBucket, String fromPath, String toBucket, String toPath);
}
