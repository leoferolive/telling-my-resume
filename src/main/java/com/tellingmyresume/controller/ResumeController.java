package com.tellingmyresume.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tellingmyresume.service.ResumeService;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/save")
    public String saveResume(@RequestParam String fileName, @RequestBody String content) throws IOException {
        resumeService.saveResume(fileName, content);
        return "Currículo salvo com sucesso!";
    }

    @GetMapping("/read/{fileName}")
    public String readResume(@PathVariable String fileName) throws IOException {
        return resumeService.readResume(fileName);
    }
}
