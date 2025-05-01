package com.atix.demo.repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atix.demo.domain.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document findByIdFile(UUID IdFile);

}
