package com.evently.model;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "forms")
public class Form {

    @Id
    private String id;
    private String title;
    private String description;
    private List<FormField> fields;
    private LocalDateTime createdAt;

    public Form() {}

    public Form(String title, String description, List<FormField> fields, LocalDateTime createdAt) {
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

    public List<FormField> getFields() { return fields; }
    public void setFields(List<FormField> fields) { this.fields = fields; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
