package com.atix.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.atix.demo.domain.ConcentrationMean;
public interface ConcentrationMeanRepository extends JpaRepository<ConcentrationMean, Long>{
    
}
