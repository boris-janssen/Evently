package com.evently.validation;

import com.evently.dto.EntryRequest;
import com.evently.exception.EntryValidationException;
import com.evently.exception.FieldError;
import com.evently.model.Form;
import com.evently.model.FormField;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class EntryValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[\\d\\s().\\-]{7,20}$");

    public void validate(Form form, EntryRequest request) {
        List<FieldError> errors = new ArrayList<>();
        Map<String, Object> answers = request.getAnswers();

        Set<String> definedFieldNames = form.getFields().stream()
                .map(FormField::getName)
                .collect(Collectors.toSet());

        // Unknown fields
        for (String key : answers.keySet()) {
            if (!definedFieldNames.contains(key)) {
                errors.add(new FieldError(key, "Unknown field: '" + key + "'"));
            }
        }

        // Per-field validation
        for (FormField field : form.getFields()) {
            String name = field.getName();
            Object value = answers.get(name);

            if (value == null || value.toString().isBlank()) {
                if (field.isRequired()) {
                    errors.add(new FieldError(name, "'" + field.getLabel() + "' is required"));
                }
                continue;
            }

            String strValue = value.toString();

            switch (field.getType()) {
                case EMAIL -> {
                    if (!EMAIL_PATTERN.matcher(strValue).matches()) {
                        errors.add(new FieldError(name, "'" + field.getLabel() + "' must be a valid email address"));
                    }
                }
                case NUMBER -> {
                    try {
                        Double.parseDouble(strValue);
                    } catch (NumberFormatException e) {
                        errors.add(new FieldError(name, "'" + field.getLabel() + "' must be a number"));
                    }
                }
                case DATE -> {
                    try {
                        LocalDate.parse(strValue);
                    } catch (DateTimeParseException e) {
                        errors.add(new FieldError(name, "'" + field.getLabel() + "' must be a valid date in YYYY-MM-DD format"));
                    }
                }
                case PHONE -> {
                    if (!PHONE_PATTERN.matcher(strValue).matches()) {
                        errors.add(new FieldError(name, "'" + field.getLabel() + "' must be a valid phone number"));
                    }
                }
                case BOOLEAN -> {
                    if (!(value instanceof Boolean) &&
                            !strValue.equalsIgnoreCase("true") &&
                            !strValue.equalsIgnoreCase("false")) {
                        errors.add(new FieldError(name, "'" + field.getLabel() + "' must be true or false"));
                    }
                }
                case TEXT -> { /* any non-blank string is valid */ }
            }
        }

        if (!errors.isEmpty()) {
            throw new EntryValidationException(errors);
        }
    }
}
