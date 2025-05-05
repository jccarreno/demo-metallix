package com.atix.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Base class for extraction requests
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExtRequestDTO {
    private UUID idFile;
    private String idTemplate;
}
