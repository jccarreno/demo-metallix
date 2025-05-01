package com.atix.demo.domain.exceptions;


/**
 * File with constants of error codes
 */
public class ErrorCodes {
    /**
     * General error code
     */
    public static final Integer GENERAL_ERROR = 1001;
    /**
     * The requested document wasn't found
     */
    public static final Integer DOCUMENT_NOT_FOUND = 1002;
    /**
     * The file extension is not supported
     */
    public static final Integer NOT_SUPPORTED_EXTENSION = 1003;
    /**
     * The extraction template wasn´t found
     */
    public static final Integer TEMPLATE_NOT_FOUND = 1004;
    /**
     * The extra data is invalid
     */
    public static Integer EXTRA_DATA_NOT_VALID = 1005;
    /**
     * External extraction error
     */
    public static Integer EXTERNAL_EXTRACTION_ERROR = 1006;
    /**
     * Error during validation
     */
    public static Integer VALIDATION_ERROR = 1007;
}

