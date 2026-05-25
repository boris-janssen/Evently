package com.evently.dto;

import com.evently.model.FieldType;

public class FormFieldResponse {

    private String name;
    private String label;
    private FieldType type;
    private boolean required;

    public FormFieldResponse() {}

    public FormFieldResponse(String name, String label, FieldType type, boolean required) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.required = required;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public FieldType getType() { return type; }
    public void setType(FieldType type) { this.type = type; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}
