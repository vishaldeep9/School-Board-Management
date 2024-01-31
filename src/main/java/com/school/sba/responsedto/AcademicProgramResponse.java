package com.school.sba.responsedto;

import java.time.LocalDate;

import com.school.sba.enums.ProgramType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcademicProgramResponse {

	private int programId;
	private ProgramType programType;
	private String programNameString;
	private LocalDate beginsAt;
	private LocalDate endsAt;
}
