package com.evently.repository;

import com.evently.model.FormEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FormEntryRepository extends MongoRepository<FormEntry, String> {

    List<FormEntry> findAllByFormId(String formId);
}
