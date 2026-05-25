package com.evently.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;


public class EntryRequest {

    @NotNull(message = "Answers are required")
    private Map<String, Object> answers;

    public Map<String, Object> getAnswers() { return answers; }
    public void setAnswers(Map<String, Object> answers) { this.answers = answers; }
}
