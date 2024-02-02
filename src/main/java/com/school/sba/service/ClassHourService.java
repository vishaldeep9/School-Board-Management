package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.entity.ClassHour;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService {

	ResponseEntity<ResponseStructure<String>> registerClassHour(int programId);

	Object updateClassHour(List<ClassHourUpdateRequest> classHourUpdateRequest);

	Object fetchRoom();

	ResponseEntity<ResponseStructure<List<ClassHour>>> createClassHourForNextWeek(int programId);

	ClassHour createNewClassHour(ClassHour classHour);

}
