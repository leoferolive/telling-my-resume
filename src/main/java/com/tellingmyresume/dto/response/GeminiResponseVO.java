package com.tellingmyresume.dto.response;

import java.util.List;

public class GeminiResponseVO {
	
    private List<Candidate> candidates;
    private UsageMetadata usageMetadata;
	
    public List<Candidate> getCandidates() {
		return candidates;
	}
	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}
	public UsageMetadata getUsageMetadata() {
		return usageMetadata;
	}
	public void setUsageMetadata(UsageMetadata usageMetadata) {
		this.usageMetadata = usageMetadata;
	}
	
}
