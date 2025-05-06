package com.atix.demo.web;

import lombok.AllArgsConstructor;

import java.io.IOException;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.atix.demo.dto.ConcentrationMeanDTO;
import com.atix.demo.dto.DetectDocResponse;
import com.atix.demo.dto.DetectUrlRequest;
import com.atix.demo.dto.ExtRequestDTO;
import com.atix.demo.dto.ExtResponseDTO;
import com.atix.demo.service.ConcentrationMeanService;
import com.atix.demo.service.document.DetectDocumentService;
import com.atix.demo.service.extraction.core.GeneralExtractionService;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class DocumentResource {

    private final DetectDocumentService detectDocumentService;
    private final GeneralExtractionService generalExtractionService;
    private final ConcentrationMeanService concentrationMeanService;


    @PostMapping("/upload-link")
    public ResponseEntity<DetectDocResponse> getSignedUrl(@RequestBody DetectUrlRequest detectUrlReq) {
        return ResponseEntity.ok(detectDocumentService.generateDetectDoc(detectUrlReq));
    }

    @PostMapping("/extract")
    public ResponseEntity<ExtResponseDTO> extract(@RequestBody ExtRequestDTO request)
            throws IOException, InterruptedException {
        Pair<ConcentrationMeanDTO, ExtResponseDTO> data = generalExtractionService.extractAndFormat(request, false);
        return ResponseEntity.ok(data.getSecond());
    }
}

