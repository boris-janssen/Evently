package com.evently.service;

import com.evently.dto.EntryRequest;
import com.evently.dto.EntryResponse;
import com.evently.dto.FormFieldResponse;
import com.evently.dto.FormRequest;
import com.evently.dto.FormResponse;
import com.evently.exception.FormNotFoundException;
import com.evently.model.Form;
import com.evently.model.FormEntry;
import com.evently.model.FormField;
import com.evently.repository.FormEntryRepository;
import com.evently.repository.FormRepository;
import com.evently.validation.EntryValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FormService {

    private final FormRepository formRepository;
    private final FormEntryRepository formEntryRepository;
    private final EntryValidator entryValidator;

    public FormService(FormRepository formRepository,
                       FormEntryRepository formEntryRepository,
                       EntryValidator entryValidator) {
        this.formRepository = formRepository;
        this.formEntryRepository = formEntryRepository;
        this.entryValidator = entryValidator;
    }

    public FormResponse createForm(FormRequest request) {
        List<FormField> fields = request.getFields().stream()
                .map(f -> new FormField(f.getName(), f.getLabel(), f.getType(), f.isRequired()))
                .toList();

        Form form = new Form(request.getTitle(), request.getDescription(), fields, LocalDateTime.now());
        Form saved = formRepository.save(form);

        return toFormResponse(saved);
    }

    public FormResponse getForm(String id) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new FormNotFoundException(id));

        return toFormResponse(form);
    }

    public EntryResponse submitEntry(String formId, EntryRequest request) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new FormNotFoundException(formId));

        entryValidator.validate(form, request);

        FormEntry entry = new FormEntry(formId, request.getAnswers(), LocalDateTime.now());
        FormEntry saved = formEntryRepository.save(entry);

        return toEntryResponse(saved);
    }

    public List<EntryResponse> getEntries(String formId) {
        if (!formRepository.existsById(formId)) {
            throw new FormNotFoundException(formId);
        }

        return formEntryRepository.findAllByFormId(formId).stream()
                .map(this::toEntryResponse)
                .toList();
    }

    private FormResponse toFormResponse(Form form) {
        List<FormFieldResponse> fieldResponses = form.getFields().stream()
                .map(f -> new FormFieldResponse(f.getName(), f.getLabel(), f.getType(), f.isRequired()))
                .toList();

        return new FormResponse(form.getId(), form.getTitle(), form.getDescription(), fieldResponses, form.getCreatedAt());
    }

    private EntryResponse toEntryResponse(FormEntry entry) {
        return new EntryResponse(entry.getEntryId(), entry.getFormId(), entry.getAnswers(), entry.getSubmittedAt());
    }
}
