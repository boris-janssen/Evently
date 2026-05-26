package com.evently;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.test.context.ActiveProfiles;

import java.net.Socket;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("systemtest")
class FormSystemTest {

    @BeforeAll
    static void requiresMongoDB() {
        boolean mongoAvailable;
        try (Socket socket = new Socket("localhost", 27017)) {
            mongoAvailable = true;
        } catch (Exception e) {
            mongoAvailable = false;
        }
        Assumptions.assumeTrue(mongoAvailable,
                "System tests require MongoDB on localhost:27017. Run: docker compose up mongodb -d");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearDatabase() {
        mongoTemplate.getDb().drop();
    }

    // -------------------------------------------------------------------------
    // Scenario 1 — POST /forms creates a form and returns 201
    // -------------------------------------------------------------------------

    @Test
    void createForm_returns201WithFormDefinition() {
        ResponseEntity<Map> response = restTemplate.postForEntity("/forms", buildFormPayload(), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsKey("id");
        assertThat(response.getBody().get("title")).isEqualTo("Spring Meetup Sign-up");
        assertThat(response.getBody().get("description")).isEqualTo("Join us for drinks and talks");
        assertThat((List<?>) response.getBody().get("fields")).hasSize(5);
    }

    // -------------------------------------------------------------------------
    // Scenario 2 — GET /forms/{id} retrieves the form definition
    // -------------------------------------------------------------------------

    @Test
    void getForm_existingId_returns200WithDefinition() {
        String id = createForm();

        ResponseEntity<Map> response = restTemplate.getForEntity("/forms/" + id, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id")).isEqualTo(id);
        assertThat(response.getBody().get("title")).isEqualTo("Spring Meetup Sign-up");
        assertThat((List<?>) response.getBody().get("fields")).hasSize(5);
    }

    // -------------------------------------------------------------------------
    // Scenario 3 — POST /forms/{id}/entries with valid data returns 201
    // -------------------------------------------------------------------------

    @Test
    void submitEntry_validAnswers_returns201WithEntry() {
        String formId = createForm();

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/forms/" + formId + "/entries",
                Map.of("answers", Map.of(
                        "name", "Jack Bauer",
                        "email", "jack@example.com",
                        "phone", "+31612345678",
                        "birthdate", "1966-02-18",
                        "newsletter", true
                )),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsKey("id");
        assertThat(response.getBody().get("formId")).isEqualTo(formId);
        Map<?, ?> answers = (Map<?, ?>) response.getBody().get("answers");
        assertThat(answers.get("name")).isEqualTo("Jack Bauer");
        assertThat(answers.get("email")).isEqualTo("jack@example.com");
    }

    // -------------------------------------------------------------------------
    // Scenario 4 — POST /forms/{id}/entries with invalid data returns 400
    //              with all errors at once (missing required, bad email, unknown)
    // -------------------------------------------------------------------------

    @Test
    void submitEntry_invalidAnswers_returns400WithAllErrors() {
        String formId = createForm();

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/forms/" + formId + "/entries",
                Map.of("answers", Map.of(
                        "email", "not-an-email",
                        "unknown", "surprise"
                )),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        List<?> errors = (List<?>) response.getBody().get("errors");
        assertThat(errors).hasSize(3);
        List<String> fields = errors.stream()
                .map(e -> (String) ((Map<?, ?>) e).get("field"))
                .toList();
        assertThat(fields).containsExactlyInAnyOrder("name", "email", "unknown");
    }

    // -------------------------------------------------------------------------
    // Scenario 5 — GET /forms/{id}/entries returns submitted entries
    // -------------------------------------------------------------------------

    @Test
    void getEntries_afterSubmission_returnsEntryList() {
        String formId = createForm();
        submitValidEntry(formId);

        ResponseEntity<List> response = restTemplate.getForEntity(
                "/forms/" + formId + "/entries", List.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        Map<?, ?> entry = (Map<?, ?>) response.getBody().get(0);
        assertThat(entry.get("formId")).isEqualTo(formId);
        assertThat(((Map<?, ?>) entry.get("answers")).get("name")).isEqualTo("Jack Bauer");
    }

    // -------------------------------------------------------------------------
    // Scenario 6 — POST /forms with missing title returns 400 (DTO validation)
    // -------------------------------------------------------------------------

    @Test
    void createForm_missingTitle_returns400WithFieldError() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/forms",
                Map.of("fields", List.of(
                        Map.of("name", "x", "label", "X", "type", "TEXT", "required", true)
                )),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        List<?> errors = (List<?>) response.getBody().get("errors");
        List<String> fields = errors.stream()
                .map(e -> (String) ((Map<?, ?>) e).get("field"))
                .toList();
        assertThat(fields).contains("title");
    }

    // -------------------------------------------------------------------------
    // Scenario 7 — GET /forms/{id} with unknown id returns 404
    // -------------------------------------------------------------------------

    @Test
    void getForm_unknownId_returns404WithErrorMessage() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/forms/doesnotexist", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat((String) response.getBody().get("error")).contains("doesnotexist");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String createForm() {
        ResponseEntity<Map> response = restTemplate.postForEntity("/forms", buildFormPayload(), Map.class);
        return (String) response.getBody().get("id");
    }

    private void submitValidEntry(String formId) {
        restTemplate.postForEntity(
                "/forms/" + formId + "/entries",
                Map.of("answers", Map.of(
                        "name", "Jack Bauer",
                        "email", "jack@example.com"
                )),
                Map.class
        );
    }

    private Map<String, Object> buildFormPayload() {
        return Map.of(
                "title", "Spring Meetup Sign-up",
                "description", "Join us for drinks and talks",
                "fields", List.of(
                        Map.of("name", "name",       "label", "Full Name",     "type", "TEXT",    "required", true),
                        Map.of("name", "email",      "label", "Email",         "type", "EMAIL",   "required", true),
                        Map.of("name", "phone",      "label", "Phone",         "type", "PHONE",   "required", false),
                        Map.of("name", "birthdate",  "label", "Date of Birth", "type", "DATE",    "required", false),
                        Map.of("name", "newsletter", "label", "Newsletter",    "type", "BOOLEAN", "required", false)
                )
        );
    }
}
