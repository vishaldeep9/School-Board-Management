package com.school.sba.entity;

import java.time.LocalDate;
import java.util.List;

import com.school.sba.enums.ProgramType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class AcademicProgram {

	@Id
	@GeneratedValue
	private int programId;
	private ProgramType programType;
	private String programName;
	private LocalDate beginsAt;
	private LocalDate endsAt;

	@ManyToOne
	private School school;

	@ManyToMany
	private List<Subject> subjects;
	
	@ManyToMany
	private List<User> users;

	@OneToMany(mappedBy = "academicProgram")
	private List<ClassHour> classHours;
}