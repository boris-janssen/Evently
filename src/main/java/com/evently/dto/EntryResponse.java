package com.evently.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class EntryResponse {

    private String id;
    private String formId;
    private Map<String, Object> answers;
    private LocalDateTime submittedAt;

    public EntryResponse() {}

    public EntryResponse(String id, String formId, Map<String, Object> answers, LocalDateTime submittedAt) {
        this.id = id;
        this.formId = formId;
        this.answers = answers;
        this.submittedAt = submittedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFormId() { return formId; }
    public void setFormId(String formId) { this.formId = formId; }

    public Map<String, Object> getAnswers() { return answers; }
    public void setAnswers(Map<String, Object> answers) { this.answers = answers; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
