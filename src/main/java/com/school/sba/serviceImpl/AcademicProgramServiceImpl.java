package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.School;
import com.school.sba.exception.UnauthorizedException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepo;
import com.school.sba.repository.SchoolRepo;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@Service
public class AcademicProgramServiceImpl implements AcademicProgramService {

	@Autowired
	AcademicProgramRepo programRepo;

	@Autowired
	SchoolRepo schoolRepo;

	@Autowired
	private ClassHourRepo classHourRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	ResponseStructure<AcademicProgramResponse> structure;

	@Autowired
	ResponseStructure<List<AcademicProgramResponse>> responseStructure;

//	@Override
//	public void mapStudentOrTeacher(int programId, int userId) {
//		User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(null, null, null));
//		AcademicProgram academicProgram = programRepo.findById(programId)
//				.orElseThrow(() -> new UserNotFoundException(null, null, null));
//		if (user.getUserRole().equals(UserRole.ADMIN)) {
//			throw new UnauthorizedException(null, null, null);
//		} else {
//			academicProgram.getUsers().add(user);
//			programRepo.save(academicProgram);
//		}
//	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> saveAcademicProgram(
			AcademicProgramRequest programRequest, int schoolId) {
		AcademicProgram program = programRepo.save(mapToAcademicProgram(programRequest));
		School school = schoolRepo.findById(schoolId).get();
		school.getAcademicPrograms().add(program);
		program.setSchool(school);
		programRepo.save(program);
		schoolRepo.save(school);
		structure.setData(mapToAcademicResponseProgram(program));
		structure.setMessage("Academic Program saved to the database");
		structure.setStatus(HttpStatus.CREATED.value());
		return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> fetchAllAcademicProgram(int schoolId) {
		School school = schoolRepo.findById(schoolId).get();
		List<AcademicProgram> listAcademicProgram = school.getAcademicPrograms();
		List<AcademicProgramResponse> responses = new ArrayList<>();
		for (AcademicProgram academicProgram : listAcademicProgram) {
			responses.add(mapToAcademicResponseProgram(academicProgram));
		}
		responseStructure.setData(responses);
		responseStructure.setMessage("Academic Program saved to the database");
		responseStructure.setStatus(HttpStatus.CREATED.value());
		return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
	}

	public AcademicProgram mapToAcademicProgram(AcademicProgramRequest programRequest) {
		return AcademicProgram.builder().programType(programRequest.getProgramType())
				.programName(programRequest.getProgramName()).beginsAt(programRequest.getBeginsAt())
				.endsAt(programRequest.getEndsAt()).build();
	}

	public AcademicProgramResponse mapToAcademicResponseProgram(AcademicProgram academicProgram) {
		return AcademicProgramResponse.builder().programId(academicProgram.getProgramId())
				.programType(academicProgram.getProgramType()).programNameString(academicProgram.getProgramName())
				.beginsAt(academicProgram.getBeginsAt()).endsAt(academicProgram.getEndsAt()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addUserToAcademicProgram(int programId,
			int userId) {
		AcademicProgram academicProgram = programRepo.findById(programId)
				.orElseThrow(() -> new UserNotFoundException("there is no data with this programId",
						HttpStatus.NOT_FOUND, "No Such Academic Program Exist in Database"));
		// map function->it simplifies the process of iterating through a
		// collection(each element in a list or array) and performing the same operation
		// on each item
		return userRepo.findById(userId).map(u -> {
//			if (u.getUserRole().equals(UserRole.ADMIN)) {
//				throw new UnauthorizedException("Admin can't add user into Academic Program", HttpStatus.BAD_REQUEST,
//						"No such mapping possible");
//				} 
//			else 
			if (!(academicProgram.getSubjects().contains(u.getSubject()))) {
				throw new UnauthorizedException("Teacher is with irrevalent Subject", HttpStatus.BAD_REQUEST,
						"Mapping is not Possible in between ");
			} else {

				// adding user to academic program
				academicProgram.getUsers().add(u);

				programRepo.save(academicProgram);

				structure.setStatus(HttpStatus.ACCEPTED.value());
				structure.setMessage("Added User to Academic Program");
				structure.setData(mapToAcademicResponseProgram(academicProgram));
				return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.ACCEPTED);
			}
		}).orElseThrow(() -> new UserNotFoundException("no data found with this id", HttpStatus.NOT_FOUND,
				"No such user in DataBase"));
	}

//	@Override
//	public void registerClassHour(int programId, int classHoursPerDay) {
//		  AcademicProgram academicProgram = programRepo.findById(programId)
//	                .orElseThrow(() -> new UserNotFoundException("Academic Program not found with id: " ,HttpStatus.NOT_FOUND,"Not Found"));
//
//	        // Assuming working days per week is 6
//	        int workingDaysPerWeek = 6;
//
//	        Schedule schedule=new Schedule();
//	        // Assuming classHourLength is in hours
//	        int classHourLengthInMinutes = schedule.getClassHoursPerDay() * 60;
//
//	        List<ClassHour> generatedClassHours = new ArrayList<>();
//
//	        for (int day = 1; day <= workingDaysPerWeek; day++) {
//	            LocalTime startTime = academicProgram.getBeginsAt();
//	            LocalTime endTime = startTime.plusMinutes(classHourLengthInMinutes);
//
//	            for (int hour = 1; hour <= classHoursPerDay; hour++) {
//	                ClassHour classHours = new ClassHour();
//	                classHours.setBeginsAt(startTime);
//	                classHours.setEndsAt(endTime);
//	                classHours.setRoomNo(academicProgram.getRoomNo());
//	                classHours.setClassStatus(ClassStatus.NOTSCHEDULED);
//
//	                generatedClassHours.add(classHours);
//
//	                // Move to the next time slot
//	                startTime = endTime;
//	                endTime = startTime.plusMinutes(classHourLengthInMinutes);
//	            }
//	        }
//
//	        // Set the generated class hours to the AcademicProgram entity
//	        academicProgram.setClassHours(generatedClassHours);
//
//	        // Save the updated AcademicProgram entity
//	        programRepo.save(academicProgram);
//		
//	}

	// NOTE , wherever will have foreign key we can delete it directly
	// NOTE, =at the time of deleting non-owner one ,we have to option
	// opt-1 --> remove connection(using null) of owner one ,opt-2--> directly delete owner then delete non-owner one
	// NOTE, if Owner one is connected to Non-owner then at the time of deleting
	// Owner, we donot have to take care about non-owner one
	@Override
	public String deleteAcademic() {
		List<AcademicProgram> program = programRepo.findByIsDeleted(true);

		program.forEach((u) -> {
			// Here classHour has the Foreign key that is why we are directly deleting it
			classHourRepo.deleteAll(u.getClassHours());
//	        		u.setSubjects(null);
//	        		u.setClassHours(null);
			programRepo.delete(u);
		});

		return "Academic Program has been deleted";
	}

}