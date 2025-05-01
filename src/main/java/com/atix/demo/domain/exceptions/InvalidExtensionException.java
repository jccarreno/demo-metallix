package com.atix.demo.domain.exceptions;

import org.slf4j.event.Level;

/**
 * Exception to be thrown when a file has an unsupported extension.
 */
public class InvalidExtensionException extends GeneralException {

    public InvalidExtensionException(String message) {
        super(message);
    }

    @Override
    public Integer getErrorCode() {
        return ErrorCodes.NOT_SUPPORTED_EXTENSION;
    }

    @Override
    public Level getLogLevel() {
        return Level.INFO;
    }
}
