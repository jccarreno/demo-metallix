package com.atix.demo.dto;
import java.math.BigDecimal;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ConcentrationMeanDTO {
     private BigDecimal Au;

    
    private BigDecimal Ag;

    private BigDecimal Pt;

    private BigDecimal Pd;

    private BigDecimal Rh;

    private BigDecimal Ru;
    
    private BigDecimal Ir;

    private BigDecimal Cu;
}
