package com.evently.dto;

import com.evently.model.FieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FormFieldRequest {

    @NotBlank(message = "Field name is required")
    private String name;

    @NotBlank(message = "Field label is required")
    private String label;

    @NotNull(message = "Field type is required")
    private FieldType type;

    private boolean required;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public FieldType getType() { return type; }
    public void setType(FieldType type) { this.type = type; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}
