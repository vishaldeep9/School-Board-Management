package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.entity.ClassHour;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@RestController
public class ClassHourController {

	@Autowired
	private ClassHourService classHourService;

	@PostMapping(path = " /academic-program/{programId}/{class-hours}")
	public ResponseEntity<ResponseStructure<String>> registerClassHour(@PathVariable int programId) {

		return classHourService.registerClassHour(programId);
	}
	
	@PutMapping(path="/class-hours")
	public Object updateClassHour(@RequestBody List<ClassHourUpdateRequest> classHourUpdateRequest){
		
		return classHourService.updateClassHour(classHourUpdateRequest);
	}
	
	@PostMapping(path="class-hours")
	public ResponseEntity<ResponseStructure<List<ClassHour>>> createClassHourForNextWeek(@PathVariable int programId){
		
		return classHourService.createClassHourForNextWeek(programId);
	}
	
	
	@PostMapping(path="class_hour")
	public ClassHour createNewClassHour(@RequestBody ClassHour classHour) {
		
		return classHourService.createNewClassHour(classHour);
	}
	
}
