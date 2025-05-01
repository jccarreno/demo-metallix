package com.atix.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetectUrlRequest {
    private String filename;
    private String password;
}