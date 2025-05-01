package com.atix.demo.utils.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error details
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorDetail {
    /**
     * Reference of the error (e.g. the field name)
     */
    private String reference;
    /**
     * Code of the error (e.g. INVALID_NUMBER)
     */
    private String errorCode;
    /**
     * Error cause or reason
     */
    private String reason;

}
