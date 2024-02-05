package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@RestController
public class AcademicProgramController {
	
	@Autowired
	AcademicProgramService programService;

	@PostMapping(path = "/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> saveAcademicProgram( @PathVariable int schoolId,@RequestBody AcademicProgramRequest req) {
		return programService.saveAcademicProgram(req,schoolId);
	}
	
	@GetMapping(path = "/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> fetchAllAcademicProgram(@PathVariable int schoolId) {
		return programService.fetchAllAcademicProgram(schoolId); 
	}
	
//	@PutMapping(path = "/academic-programs/{programId}/users/{userId}")
//	public void mapStudentOrTeacher(@PathVariable int programId,@PathVariable int userId) {
//		programService.mapStudentOrTeacher(programId,userId);
//	}
//	
	
	@PostMapping(path="/academic-programs/{programId}/users/{userId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addUserToAcademicProgram(@PathVariable int programId, @PathVariable int userId){
		
		
		return programService.addUserToAcademicProgram(programId,userId);
	}
	


	
}
	