package com.evently.controller;

import com.evently.dto.EntryRequest;
import com.evently.dto.EntryResponse;
import com.evently.dto.FormRequest;
import com.evently.dto.FormResponse;
import com.evently.dto.FormSummaryResponse;
import com.evently.service.FormService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/forms")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormResponse createForm(@Valid @RequestBody FormRequest request) {
        return formService.createForm(request);
    }

    @GetMapping
    public List<FormSummaryResponse> getForms() {
        return formService.getForms();
    }

    @GetMapping("/{id}")
    public FormResponse getForm(@PathVariable String id) {
        return formService.getForm(id);
    }

    @PostMapping("/{id}/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public EntryResponse submitEntry(@PathVariable String id, @RequestBody EntryRequest request) {
        return formService.submitEntry(id, request);
    }

    @GetMapping("/{id}/entries")
    public List<EntryResponse> getEntries(@PathVariable String id) {
        return formService.getEntries(id);
    }
}
