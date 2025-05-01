package com.atix.demo.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.slf4j.event.Level; // Asegúrate de importar esto
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception thrown when a template is not registered in the system
 */
public class TemplateNotFoundException extends GeneralException {

    private static final Logger logger = LoggerFactory.getLogger(TemplateNotFoundException.class);

    public TemplateNotFoundException(String message) {
        super(message);
    }

    @Override
    public Integer getErrorCode() {
        return ErrorCodes.TEMPLATE_NOT_FOUND;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }

    @Override
    public Level getLogLevel() {
        return Level.WARN; // Usa el nivel adecuado de SLF4J
    }

    @Override
    public void logException() {
        // Puedes llamar a este método en tu ExceptionInterceptor para registrar la excepción
        Level logLevel = getLogLevel();
        String message = getMessage();
        switch (logLevel) {
            case INFO:
                logger.info(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case TRACE:
                logger.trace(message);
                break;
            default:
                logger.error("Unknown logging level: {}", logLevel);
                break;
        }
    }
}
