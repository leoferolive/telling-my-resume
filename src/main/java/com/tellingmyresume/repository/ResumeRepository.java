package com.tellingmyresume.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tellingmyresume.model.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

	public Resume findByToken(String token);	
	
	Resume findByFileName(String fileName);
	boolean existsByFileName(String fileName);
	
}
