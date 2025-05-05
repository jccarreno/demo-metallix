package com.atix.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Base class for extraction responses
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExtResponseDTO {
    private UUID idFile;
    private Object data;
    private String signedLink;
}
