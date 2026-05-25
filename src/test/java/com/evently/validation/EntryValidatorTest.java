package com.evently.validation;

import com.evently.dto.EntryRequest;
import com.evently.exception.EntryValidationException;
import com.evently.exception.FieldError;
import com.evently.model.FieldType;
import com.evently.model.Form;
import com.evently.model.FormField;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class EntryValidatorTest {

    private EntryValidator validator;
    private Form form;

    @BeforeEach
    void setUp() {
        validator = new EntryValidator();
        form = new Form();
        form.setFields(List.of(
                new FormField("name",  "Full Name",    FieldType.TEXT,    true),
                new FormField("email", "Email",        FieldType.EMAIL,   true),
                new FormField("age",   "Age",          FieldType.NUMBER,  false),
                new FormField("dob",   "Date of Birth",FieldType.DATE,    false),
                new FormField("phone", "Phone",        FieldType.PHONE,   false),
                new FormField("newsletter", "Newsletter", FieldType.BOOLEAN, false)
        ));
    }

    private EntryRequest requestWith(Map<String, Object> answers) {
        EntryRequest req = new EntryRequest();
        req.setAnswers(answers);
        return req;
    }

    // --- Happy path ---

    @Test
    void validEntry_doesNotThrow() {
        validator.validate(form, requestWith(Map.of(
                "name", "Jack Bauer",
                "email", "jack@example.com",
                "age", 30,
                "dob", "1995-06-15",
                "phone", "+31612345678",
                "newsletter", true
        )));
    }

    @Test
    void validEntry_optionalFieldsOmitted_doesNotThrow() {
        validator.validate(form, requestWith(Map.of(
                "name", "Jack Bauer",
                "email", "jack@example.com"
        )));
    }

    // --- Required field missing ---

    @Test
    void missingRequiredField_throwsWithFieldError() {
        EntryValidationException ex = catchThrowableOfType(
                () -> validator.validate(form, requestWith(Map.of("email", "jack@example.com"))),
                EntryValidationException.class
        );
        assertThat(ex.getErrors()).extracting(FieldError::field).contains("name");
    }

    @Test
    void allRequiredFieldsMissing_throwsAllErrors() {
        EntryValidationException ex = catchThrowableOfType(
                () -> validator.validate(form, requestWith(Map.of())),
                EntryValidationException.class
        );
        assertThat(ex.getErrors()).extracting(FieldError::field).containsExactlyInAnyOrder("name", "email");
    }

    // --- Unknown field ---

    @Test
    void unknownField_throwsWithFieldError() {
        EntryValidationException ex = catchThrowableOfType(
                () -> validator.validate(form, requestWith(Map.of(
                        "name", "Jack Bauer",
                        "email", "jack@example.com",
                        "unknownField", "surprise"
                ))),
                EntryValidationException.class
        );
        assertThat(ex.getErrors()).extracting(FieldError::field).contains("unknownField");
    }

    // --- EMAIL ---

    @Test
    void invalidEmail_throwsWithFieldError() {
        EntryValidationException ex = catchThrowableOfType(
                () -> validator.validate(form, requestWith(Map.of(
                        "name", "Jack Bauer",
                        "email", "not-an-email"
                ))),
                EntryValidationException.class
        );
        assertThat(ex.getErrors()).extracting(FieldError::field).contains("email");
    }

    @Test
    void validEmail_doesNotThrow() {
        validator.validate(form, requestWith(Map.of(
                "name", "Jack Bauer",
                "email", "jack@example.com"
        )));
    }

    // --- NUMBER ---

    @Test
    void invalidNumber_throwsWithFieldError() {
        EntryValidationException ex = catchThrowableOfType(
                () -> validator.validate(form, requestWith(Map.of(
                        "name", "Jack Bauer",
                        "email", "jack@example.com",
                        "age", "notanumber"
                ))),
                EntryValidationException.class
        );
        assertThat(ex.getErrors()).extracting(FieldError::field).contains("age");
    }

    @Test
    void validNumber_doesNotThrow() {
        validator.validate(form, requestWith(Map.of(
                "name", "Jack Bauer",
                "email", "jack@example.com",
                "age", 25
        )));
    }

    // --- DATE ---

    @Test
    void invalidDate_throwsWithFieldError() {
        EntryValidationException ex = catchThrowableOfType(
                () -> validator.validate(form, requestWith(Map.of(
                        "name", "Jack Bauer",
                        "email", "jack@example.com",
                        "dob", "1995-13-40"
                ))),
                EntryValidationException.class
        );
        assertThat(ex.getErrors()).extracting(FieldError::field).contains("dob");
    }

    @Test
    void validDate_doesNotThrow() {
        validator.validate(form, requestWith(Map.of(
                "name", "Jack Bauer",
                "email", "jack@example.com",
                "dob", "1995-06-15"
        )));
    }

    // --- PHONE ---

    @Test
    void invalidPhone_throwsWithFieldError() {
        EntryValidationException ex = catchThrowableOfType(
                () -> validator.validate(form, requestWith(Map.of(
                        "name", "Jack Bauer",
                        "email", "jack@example.com",
                        "phone", "abc"
                ))),
                EntryValidationException.class
        );
        assertThat(ex.getErrors()).extracting(FieldError::field).contains("phone");
    }

    @Test
    void validPhone_doesNotThrow() {
        validator.validate(form, requestWith(Map.of(
                "name", "Jack Bauer",
                "email", "jack@example.com",
                "phone", "+31612345678"
        )));
    }

    // --- BOOLEAN ---

    @Test
    void invalidBoolean_throwsWithFieldError() {
        EntryValidationException ex = catchThrowableOfType(
                () -> validator.validate(form, requestWith(Map.of(
                        "name", "Jack Bauer",
                        "email", "jack@example.com",
                        "newsletter", "yes"
                ))),
                EntryValidationException.class
        );
        assertThat(ex.getErrors()).extracting(FieldError::field).contains("newsletter");
    }

    @Test
    void validBoolean_asString_doesNotThrow() {
        validator.validate(form, requestWith(Map.of(
                "name", "Jack Bauer",
                "email", "jack@example.com",
                "newsletter", "true"
        )));
    }

    @Test
    void validBoolean_asNativeBoolean_doesNotThrow() {
        validator.validate(form, requestWith(Map.of(
                "name", "Jack Bauer",
                "email", "jack@example.com",
                "newsletter", false
        )));
    }

    // --- Multiple errors returned at once ---

    @Test
    void multipleErrors_allReturnedAtOnce() {
        EntryValidationException ex = catchThrowableOfType(
                () -> validator.validate(form, requestWith(Map.of(
                        "email", "not-an-email",
                        "age", "notanumber",
                        "dob", "bad-date"
                ))),
                EntryValidationException.class
        );
        assertThat(ex.getErrors()).extracting(FieldError::field)
                .containsExactlyInAnyOrder("name", "email", "age", "dob");
    }
}
