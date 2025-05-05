package com.atix.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atix.demo.domain.DocumentTemplate;

public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate,String>{

}
