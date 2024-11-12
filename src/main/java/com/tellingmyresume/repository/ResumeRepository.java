package com.tellingmyresume.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tellingmyresume.model.Resume;


public interface ResumeRepository extends JpaRepository<Resume, Long> {

	public Resume findByToken(String token);	
	
}
