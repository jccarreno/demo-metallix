package com.atix.demo.domain;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Representa una nota de crédito en el sistema.
 * Esta entidad se mapea a la tabla "CreditNote" en la base de datos.
 * Contiene información relacionada con una nota de crédito emitida, como el número, la fecha y el valor total de la misma.
 */
@Entity
@Table(name = "ConcentrationMean")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcentrationMean {
    /**
     * Identificador único de la nota de crédito.
     * Se genera automáticamente mediante la estrategia de incremento de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="MeasuringTime")
    private String MeasuringTime;

    @Column(name="LotNumber")
    private String LotNumber;
    
    @Column(name="WorkNumber")
    private String WorkNumber;

    @Column(name="Date")
    private String Date;

    @Column(name="Time")
    private String Time;

    @Column(name="Au")
    private BigDecimal Au;

    
    @Column(name="Ag")
    private BigDecimal Ag;

    
    @Column(name="Pt")
    private BigDecimal Pt;

    
    @Column(name="Pd")
    private BigDecimal Pd;

    
    @Column(name="Rh")
    private BigDecimal Rh;

    
    @Column(name="Ru")
    private BigDecimal Ru;
    
    @Column(name="Ir")
    private BigDecimal Ir;
    
    @Column(name="Cu")
    private BigDecimal Cu;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
}
