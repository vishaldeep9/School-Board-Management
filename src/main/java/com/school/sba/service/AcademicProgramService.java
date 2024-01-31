package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.util.ResponseStructure;

public interface AcademicProgramService {

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> saveAcademicProgram(AcademicProgramRequest req, int schoolId);

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> fetchAllAcademicProgram(int schoolId);

//	void mapStudentOrTeacher(int programId, int userId);

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> addUserToAcademicProgram(int programId, int userId);

	String deleteAcademic();



	

}
