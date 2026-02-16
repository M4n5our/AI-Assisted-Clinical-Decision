package com.cdss.service;

import com.cdss.dto.TemplateDto;
import com.cdss.model.Template;
import com.cdss.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public List<TemplateDto> getAllTemplates() {
        return templateRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TemplateDto> getTemplatesByCategory(String category) {
        return templateRepository.findByCategoryIgnoreCase(category)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public TemplateDto getTemplate(Long id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        return toDto(template);
    }

    public TemplateDto createTemplate(TemplateDto dto) {
        Template template = new Template();
        template.setName(dto.getName());
        template.setContent(dto.getContent());
        template.setCategory(dto.getCategory());
        return toDto(templateRepository.save(template));
    }

    public TemplateDto updateTemplate(Long id, TemplateDto dto) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        template.setName(dto.getName());
        template.setContent(dto.getContent());
        template.setCategory(dto.getCategory());
        return toDto(templateRepository.save(template));
    }

    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("Template not found");
        }
        templateRepository.deleteById(id);
    }

    private TemplateDto toDto(Template template) {
        TemplateDto dto = new TemplateDto();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setContent(template.getContent());
        dto.setCategory(template.getCategory());
        dto.setCreatedAt(template.getCreatedAt() != null ? template.getCreatedAt().toString() : null);
        return dto;
    }
}
