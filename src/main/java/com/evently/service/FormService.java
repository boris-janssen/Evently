package com.evently.service;

import com.evently.dto.FormFieldResponse;
import com.evently.dto.FormRequest;
import com.evently.dto.FormResponse;
import com.evently.exception.FormNotFoundException;
import com.evently.model.Form;
import com.evently.model.FormField;
import com.evently.repository.FormRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FormService {

    private final FormRepository formRepository;

    public FormService(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    public FormResponse createForm(FormRequest request) {
        List<FormField> fields = request.getFields().stream()
                .map(f -> new FormField(f.getName(), f.getLabel(), f.getType(), f.isRequired()))
                .toList();

        Form form = new Form(request.getTitle(), request.getDescription(), fields, LocalDateTime.now());
        Form saved = formRepository.save(form);

        return toResponse(saved);
    }

    public FormResponse getForm(String id) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new FormNotFoundException(id));

        return toResponse(form);
    }

    private FormResponse toResponse(Form form) {
        List<FormFieldResponse> fieldResponses = form.getFields().stream()
                .map(f -> new FormFieldResponse(f.getName(), f.getLabel(), f.getType(), f.isRequired()))
                .toList();

        return new FormResponse(form.getId(), form.getTitle(), form.getDescription(), fieldResponses, form.getCreatedAt());
    }
}
