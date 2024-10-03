package com.tellingmyresume.controller;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tellingmyresume.formatter.ResumeFormatter;
import com.tellingmyresume.service.GeminiService;
import com.tellingmyresume.service.ResumeService;
import com.tellingmyresume.vo.ResumeViewData;

@Controller
@RequestMapping("/resume")
public class ResumeViewController {

    private final ResumeService resumeService;
    private final GeminiService geminiService;

    public ResumeViewController(ResumeService resumeService, GeminiService geminiService) {
        this.resumeService = resumeService;
        this.geminiService = geminiService;
    }

    @GetMapping("/view/{fileName}")
    public String viewResume(@PathVariable String fileName, Model model) throws IOException {

        ResumeViewData data = prepareResumeData(fileName);

        model.addAttribute("resume", data.getFormattedResume());
        model.addAttribute("strengths", Arrays.asList(
            "Experiência consolidada em Java",
            "Desenvolvimento de microserviços",
            "Conhecimento de DevOps"
        ));

        return "resumeView";
    }

    private ResumeViewData prepareResumeData(String fileName) throws IOException {
        String resumeContent = resumeService.readResume(fileName);
        String generatedResume = geminiService.generateResume(resumeContent);
        String formattedResume = ResumeFormatter.formatResume(generatedResume);

        return new ResumeViewData(formattedResume);
    }
}