package com.atix.demo.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.slf4j.event.Level;

/**
 * Exception to be thrown when a document is not registered in the system.
 */
public class DocumentNotFoundException extends GeneralException {

    public DocumentNotFoundException(String message) {
        super(message);
    }

    @Override
    public Integer getErrorCode() {
        return ErrorCodes.DOCUMENT_NOT_FOUND;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public Level getLogLevel() {
        return Level.WARN;
    }

}
