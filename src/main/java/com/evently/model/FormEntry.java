package com.evently.model;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "form_entries")
public class FormEntry {

    @Id
    private String entryId;
    private String formId;
    private Map<String, Object> answers;
    private LocalDateTime submittedAt;

    public FormEntry() {}

    public FormEntry(String formId, Map<String, Object> answers, LocalDateTime submittedAt) {
        this.formId = formId;
        this.answers = answers;
        this.submittedAt = submittedAt;
    }

    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public String getFormId() { return formId; }
    public void setFormId(String formId) { this.formId = formId; }

    public Map<String, Object> getAnswers() { return answers; }
    public void setAnswers(Map<String, Object> answers) { this.answers = answers; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
