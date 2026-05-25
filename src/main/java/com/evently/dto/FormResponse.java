package com.evently.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FormResponse {

    private String id;
    private String title;
    private String description;
    private List<FormFieldResponse> fields;
    private LocalDateTime createdAt;

    public FormResponse() {}

    public FormResponse(String id, String title, String description, List<FormFieldResponse> fields, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fields = fields;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<FormFieldResponse> getFields() { return fields; }
    public void setFields(List<FormFieldResponse> fields) { this.fields = fields; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
