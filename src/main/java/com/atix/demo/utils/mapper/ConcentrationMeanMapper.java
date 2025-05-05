package com.atix.demo.utils.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.atix.demo.domain.ConcentrationMean;
import com.atix.demo.dto.ConcentrationMeanDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConcentrationMeanMapper {
    ConcentrationMeanMapper INSTANCE =Mappers.getMapper(ConcentrationMeanMapper.class);

    ConcentrationMeanDTO toDTO(ConcentrationMean concentrationMean);
    ConcentrationMean toEntity(ConcentrationMeanDTO concentrationMeanDTO);
}
