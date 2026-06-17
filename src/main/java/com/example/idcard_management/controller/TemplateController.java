package com.example.idcard_management.controller;

import com.example.idcard_management.model.Template;
import com.example.idcard_management.service.TemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public ResponseEntity<List<Template>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Template> getTemplateById(@PathVariable Long id) {
        return templateService.getTemplateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Template> getTemplateByCode(@PathVariable String code) {
        return templateService.getTemplateByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Template> createTemplate(@RequestBody Template template) {
        Template savedTemplate = templateService.createTemplate(template);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTemplate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Template> updateTemplate(@PathVariable Long id, @RequestBody Template template) {
        try {
            Template updatedTemplate = templateService.updateTemplate(id, template);
            return ResponseEntity.ok(updatedTemplate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        if (templateService.getTemplateById(id).isPresent()) {
            templateService.deleteTemplate(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
