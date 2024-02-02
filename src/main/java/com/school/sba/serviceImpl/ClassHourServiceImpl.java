package com.school.sba.serviceImpl;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.ClassStatus;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademyProgramNotFoundByIdException;
import com.school.sba.exception.ConstraintViolationException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepo;
import com.school.sba.repository.SubjectRepo;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponceEntityProxy;
import com.school.sba.util.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService {

	@Autowired
	private SubjectRepo subjectRepo;

	@Autowired
	private ClassHourRepo classHourRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private AcademicProgramRepo academicProgramRepo;

	@Autowired
	private ResponseStructure<String> rstructure;

	@Autowired
	private ResponseStructure<List<ClassHour>> structure;

//	public ClassHour mapToClassHour(ClassHourRequest classHourRequest) {
//
//		return ClassHour.builder().beginsAt(classHourRequest.getBeginsAt()).endsAt(classHourRequest.getEndsAt())
//				.roomNo(classHourRequest.getRoomNo()).classStatus(classHourRequest.getClassStatus()).build();
//	}
//
//	public ClassHourResponce mapToResponce(ClassHour classHour) {
//		return ClassHourResponce.builder().beginsAt(classHour.getBeginsAt()).endsAt(classHour.getEndsAt())
//				.roomNo(classHour.getRoomNo()).classStatus(classHour.getClassStatus()).build();
//	}

	public boolean isBreakTime(LocalDateTime beginsAt, LocalDateTime endsAt, Schedule schedule) {
		LocalTime breakTimeStart = schedule.getBreakTime();
		return ((breakTimeStart.isAfter(beginsAt.toLocalTime()) && breakTimeStart.isBefore(endsAt.toLocalTime()))
				|| breakTimeStart.equals(beginsAt.toLocalTime()));
	}

	public boolean isLunchTime(LocalDateTime beginsAt, LocalDateTime endsAt, Schedule schedule) {
		LocalTime lunchTimeStart = schedule.getLunchTime();
		return ((lunchTimeStart.isAfter(beginsAt.toLocalTime()) && lunchTimeStart.isBefore(endsAt.toLocalTime()))
				|| lunchTimeStart.equals(beginsAt.toLocalTime()));
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> registerClassHour(int programId) {

		return academicProgramRepo.findById(programId).map(academicProgarm -> {
			School school = academicProgarm.getSchool();
			Schedule schedule = school.getSchedule();
			if (schedule != null) {
				int classHourPerDay = schedule.getClassHoursPerDay();
				int classHourLength = (int) schedule.getClassHourInMinutes().toMinutes();

				LocalDateTime currentTime = LocalDateTime.now().with(schedule.getOpensAt());

				LocalDateTime lunchTimeStart = LocalDateTime.now().with(schedule.getLunchTime());
				LocalDateTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthInMinutes().toMinutes());
				LocalDateTime breakTimeStart = LocalDateTime.now().with(schedule.getBreakTime());
				LocalDateTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthInMinutes().toMinutes());
				LocalDateTime nextSaturday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
						.plusDays(7);

				while (currentTime.isBefore(nextSaturday) && currentTime.getDayOfWeek() != DayOfWeek.SUNDAY) {

					for (int day = 1; day <= 6; day++) {

						// 2 is total break time
						for (int hour = 1; hour <= classHourPerDay + 2; hour++) {
							ClassHour classHour = new ClassHour();
							LocalDateTime beginsAt = currentTime;
							LocalDateTime endsAt = beginsAt.plusMinutes(classHourLength);

							if (!isLunchTime(beginsAt, endsAt, schedule)) {
								if (!isBreakTime(beginsAt, endsAt, schedule)) {
									classHour.setBeginsAt(beginsAt);
									classHour.setEndsAt(endsAt);
									classHour.setClassStatus(ClassStatus.ONGOING);
									currentTime = endsAt;

								} else {
									classHour.setBeginsAt(breakTimeStart);
									classHour.setEndsAt(breakTimeEnd);
									classHour.setClassStatus(ClassStatus.BREAK_TIME);
									currentTime = breakTimeEnd;
								}
							} else {
								classHour.setBeginsAt(lunchTimeStart);
								classHour.setEndsAt(lunchTimeEnd);
								classHour.setClassStatus(ClassStatus.LUNCH_TIME);
								currentTime = lunchTimeEnd;
							}
							classHour.setAcademicProgram(academicProgarm);
							classHourRepo.save(classHour);
						}
					}
					currentTime = currentTime.plusDays(1).with(schedule.getOpensAt());
				}
			} else
				throw new UserNotFoundException(null, null, null);
			return ResponceEntityProxy.getResponseEntity(HttpStatus.CREATED, "", "");
		}).orElseThrow(() -> new AcademyProgramNotFoundByIdException("program not found by this Id"));
	}

	@Override
	public Object updateClassHour(List<ClassHourUpdateRequest> classHourUpdateRequest) {

		classHourUpdateRequest.forEach((request) -> {
			int userId = request.getUserId();
			userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found by this id",
					HttpStatus.NOT_FOUND, "No User present in data base throgh this id"));

			int classHourId = request.getClassHourId();
			classHourRepo.findById(classHourId).orElseThrow(() -> new UserNotFoundException(null, null, null));

			int roomNo = request.getRoomNo();
			classHourRepo.findById(roomNo).orElseThrow(() -> new UserNotFoundException(null, null, null));

			int subjectId = request.getSubjectId();
			Subject subject2 = subjectRepo.findById(subjectId)
					.orElseThrow(() -> new UserNotFoundException(null, null, null));

			User user = new User();
			ClassHour classHour = new ClassHour();

			if (!classHourRepo.existsByBeginsAtBetweenAndRoomNo(classHour.getBeginsAt().minusMinutes(1),
					classHour.getEndsAt().plusMinutes(1), roomNo)) {

				if (user.getUserRole().equals(UserRole.TEACHER)) {
					classHour.setClassHourId(classHourId);
					classHour.setRoomNo(roomNo);
					classHour.setSubject(subject2);
					classHour.setUser(user);
					classHourRepo.save(classHour);
				} else {
					throw new ConstraintViolationException("null", HttpStatus.BAD_REQUEST, "null");
				}
			} else {
				throw new UserNotFoundException("this user ID is not avaliable", HttpStatus.NOT_FOUND,
						"user Not in the data base");
			}
		});

		return "Updated ClassHour";
	}

	@Override
	public Object fetchRoom() {
		LocalDateTime dateTime = LocalDateTime.of(2023, 9, 12, 8, 30);
		LocalDateTime endTime = LocalDateTime.of(2023, 9, 13, 8, 30);

		return classHourRepo.existsByBeginsAtBetweenAndRoomNo(dateTime, endTime, 100);
	}

	@Override
	public ResponseEntity<ResponseStructure<List<ClassHour>>> createClassHourForNextWeek(int programId) {

		AcademicProgram academicProgram = academicProgramRepo.findById(programId).get();
		List<ClassHour> classHours = academicProgram.getClassHours();
		classHours.forEach((cl) -> {
			// createNewClassHour(ClassHour classHour) is down side we created
			ClassHour createNewClassHour = createNewClassHour(cl);
			classHours.add(createNewClassHour);
		});

		classHours.forEach((hour) -> {
			LocalDateTime plusDays = hour.getBeginsAt().plusDays(7);
			hour.setBeginsAt(plusDays);
			classHourRepo.save(hour);
		});
		rstructure.setData("Class Hour Generated");
		structure.setMessage("New Class Hour Created For Next Week");
		structure.setStatus(HttpStatus.CREATED.value());

		return new ResponseEntity<ResponseStructure<List<ClassHour>>>(structure, HttpStatus.CREATED);
	}

	@Override
	public ClassHour createNewClassHour(ClassHour cl) {
		ClassHour classHour2 = new ClassHour();

		classHour2.setAcademicProgram(cl.getAcademicProgram());
		classHour2.setBeginsAt(cl.getBeginsAt());
		classHour2.setClassStatus(cl.getClassStatus());
		classHour2.setEndsAt(cl.getEndsAt());
		classHour2.setRoomNo(cl.getRoomNo());
		classHour2.setSubject(cl.getSubject());
		classHour2.setUser(cl.getUser());

		return classHour2;
	}
}
