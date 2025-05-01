package com.atix.demo.web;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.atix.demo.dto.DetectDocResponse;
import com.atix.demo.dto.DetectUrlRequest;
import com.atix.demo.service.document.DetectDocumentService;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class DocumentResource {

    private final DetectDocumentService detectDocumentService;


    @PostMapping("/upload-link")
    public ResponseEntity<DetectDocResponse> getSignedUrl(@RequestBody DetectUrlRequest detectUrlReq) {
        return ResponseEntity.ok(detectDocumentService.generateDetectDoc(detectUrlReq));
    }

}

