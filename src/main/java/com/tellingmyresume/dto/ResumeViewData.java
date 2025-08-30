package com.tellingmyresume.dto;

public class ResumeViewData {

    private final String formattedResume;

    public ResumeViewData(String formattedResume) {
        this.formattedResume = formattedResume;
    }

    public String getFormattedResume() {
        return formattedResume;
    }
}
