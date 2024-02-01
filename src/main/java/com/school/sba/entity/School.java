package com.school.sba.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class School {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int schoolId;
	private String schoolName;
	private long contactNo;
	private String emailId;
	private String address;

	
	//Cascade.remove means deleting a record in a parent table automatically deletes all related records in child tables. 
	@OneToOne(cascade = CascadeType.REMOVE)
	private Schedule schedule;
	
	@OneToMany
	private List<AcademicProgram> academicPrograms;
	
}