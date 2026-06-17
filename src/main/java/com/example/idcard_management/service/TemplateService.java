package com.example.idcard_management.service;

import com.example.idcard_management.model.Template;
import com.example.idcard_management.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    public Optional<Template> getTemplateById(Long id) {
        return templateRepository.findById(id);
    }

    public Optional<Template> getTemplateByCode(String code) {
        return templateRepository.findByCode(code);
    }

    public Template createTemplate(Template template) {
        return templateRepository.save(template);
    }

    public Template updateTemplate(Long id, Template templateDetails) {
        return templateRepository.findById(id)
                .map(template -> {
                    template.setCode(templateDetails.getCode());
                    template.setName(templateDetails.getName());
                    template.setOrganizationName(templateDetails.getOrganizationName());
                    template.setLayout(templateDetails.getLayout());
                    template.setPrimaryColor(templateDetails.getPrimaryColor());
                    template.setSecondaryColor(templateDetails.getSecondaryColor());
                    template.setTextColor(templateDetails.getTextColor());
                    template.setTagline(templateDetails.getTagline());
                    return templateRepository.save(template);
                })
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
    }

    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    public boolean existsByCode(String code) {
        return templateRepository.existsByCode(code);
    }
}
