package com.tellingmyresume.controller;

import com.tellingmyresume.dto.ResumeViewData;
import com.tellingmyresume.dto.response.ResumeAnalysisResponse;
import com.tellingmyresume.exception.AIServiceException;
import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.formatter.ResumeFormatter;
import com.tellingmyresume.service.ResumeAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/resume")
public class ResumeViewController {

    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeViewController(ResumeAnalysisService resumeAnalysisService) {
        this.resumeAnalysisService = resumeAnalysisService;
    }

    @GetMapping("/view/{fileName}")
    public String viewResume(@PathVariable String fileName, Model model) {
        ResumeViewData data = prepareResumeData(fileName);

        model.addAttribute("resume", data.getFormattedResume());
        model.addAttribute("strengths", Arrays.asList(
            "Experiência consolidada em Java",
            "Desenvolvimento de microserviços",
            "Conhecimento de DevOps"
        ));

        return "resumeView";
    }

    private ResumeViewData prepareResumeData(String fileName) {
        ResumeAnalysisResponse analysisResponse = resumeAnalysisService.analyzeResumeWithBestAvailable(fileName);
        String formattedResume = ResumeFormatter.formatResume(analysisResponse.getAnalysis());

        return new ResumeViewData(formattedResume);
    }
}