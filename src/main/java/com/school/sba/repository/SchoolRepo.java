package com.school.sba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.School;

public interface SchoolRepo extends JpaRepository<School, Integer> {

	boolean existsBySchoolId(int schoolId);

	List<School> findByIsDeleted(boolean b);
}