package com.atix.demo.domain.exceptions;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import com.atix.demo.utils.error.ErrorDetail;

/**
 * General application exception. All custom exceptions
 * must inherit from this class.
 */
public abstract class GeneralException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(GeneralException.class);

    public GeneralException(String message) {
        super(message);
    }

    /**
     * Allows to obtain the error code of the exception. It is used to notify
     * the user of the specific type of error. This code must be included in
     * the Error Code List.
     *
     * @return the error code
     */
    public abstract Integer getErrorCode();

    /**
     * Gets the HTTP status associated with the exception. By default, it returns
     * BAD_REQUEST, but can be overridden by concrete implementations.
     *
     * @return the HTTP status associated with the exception
     */
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * Gets the appropriate logging level for the exception.
     *
     * @return the logging level
     * @see Level
     */
    public abstract Level getLogLevel();

    /**
     * Indicates if the full exception trace should be logged ({@code true})
     * or if only the message should be logged. Defaults to {@code true}.
     *
     * @return whether to log the full trace
     */
    public boolean shouldLogThrowable() {
        return true;
    }

    /**
     * Gets the list of error details. Defaults to an empty list.
     *
     * @return the list of error details
     */
    public List<ErrorDetail> getDetails() {
        return Collections.emptyList();
    }

    /**
     * Logs the exception based on the logging level and whether the throwable should be logged.
     */
    public void logException() {
        Level logLevel = getLogLevel();
        String message = getMessage();
        Throwable throwable = shouldLogThrowable() ? this : null;

        switch (logLevel) {
            case INFO:
                logger.info(message, throwable);
                break;
            case DEBUG:
                logger.debug(message, throwable);
                break;
            case ERROR:
                logger.error(message, throwable);
                break;
            case WARN:
                logger.warn(message, throwable);
                break;
            case TRACE:
                logger.trace(message, throwable);
                break;
            default:
                logger.error("Unknown logging level: {}", logLevel);
                break;
        }
    }
}
