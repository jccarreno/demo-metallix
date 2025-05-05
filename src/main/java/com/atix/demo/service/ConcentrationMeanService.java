package com.atix.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atix.demo.domain.ConcentrationMean;
import com.atix.demo.domain.Document;
import com.atix.demo.dto.ConcentrationMeanDTO;
import com.atix.demo.repository.ConcentrationMeanRepository;
import com.atix.demo.service.document.DocumentService;
import com.atix.demo.utils.mapper.ConcentrationMeanMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ConcentrationMeanService {
    private final ConcentrationMeanRepository concentrationMeanRepository;
    private final DocumentService documentService;
    private final ConcentrationMeanMapper mapper;

    @Transactional
    public void save(ConcentrationMeanDTO creditNoteDTO, Document document) {
        ConcentrationMean creditNote = mapper.toEntity(creditNoteDTO);
        creditNote.setDocument(document);

        save(creditNote);
    }

    public ConcentrationMean save(ConcentrationMean concentrationMean)
    {
        return concentrationMeanRepository.save(concentrationMean);
    }
}
