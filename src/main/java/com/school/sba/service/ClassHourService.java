package com.school.sba.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.entity.ClassHour;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.requestdto.ExcelRequest;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService {

	ResponseEntity<ResponseStructure<String>> registerClassHour(int programId);

	Object updateClassHour(List<ClassHourUpdateRequest> classHourUpdateRequest);

	Object fetchRoom();

	ResponseEntity<ResponseStructure<List<ClassHour>>> createClassHourForNextWeek(int programId);

	ClassHour createNewClassHour(ClassHour classHour);

	ResponseEntity<ResponseStructure<String>> printData(ExcelRequest excelRequest, int programId);

	ResponseEntity<?> writeToExcel(MultipartFile file, int programId, LocalDate fromDate, LocalDate toDate) throws IOException ;

}
