package com.evently.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;


public class FormRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotEmpty(message = "At least one field is required")
    @Valid
    private List<FormFieldRequest> fields;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<FormFieldRequest> getFields() { return fields; }
    public void setFields(List<FormFieldRequest> fields) { this.fields = fields; }
}
