package com.school.sba.serviceImpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import com.school.sba.requestdto.ExcelRequest;
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

	@Override
	public ResponseEntity<ResponseStructure<String>> printData(ExcelRequest excelRequest, int programId) {

		AcademicProgram academicProgram = academicProgramRepo.findById(programId).orElseThrow(
				() -> new UserNotFoundException("No user With This Id", HttpStatus.NOT_FOUND, "No User In DatBAse"));

//		String filePath = "C:\\Users\\LENOVO\\OneDrive\\Desktop";
//		String fileFolder = filePath + "/text.xlsx";

		LocalDateTime from = excelRequest.getFromDate().atTime(LocalTime.MIDNIGHT);
		LocalDateTime to = excelRequest.getToDate().atTime(LocalTime.MIDNIGHT).plusDays(1);
		List<ClassHour> classhours = classHourRepo.findByAcademicProgramAndBeginsAtBetween(academicProgram, from, to);

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet createSheet = workbook.createSheet();

		int rowNo = 0;
		XSSFRow header = createSheet.createRow(rowNo);

		// creating Row Heading
		header.createCell(0).setCellValue("Class Hour Id");
		header.createCell(1).setCellValue("from");
		header.createCell(2).setCellValue("to");
		header.createCell(3).setCellValue("Subject");
		header.createCell(4).setCellValue("User");
		header.createCell(5).setCellValue("RoomNo");

		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH-mm");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd");

		for (ClassHour classHour : classhours) {

			XSSFRow row = createSheet.createRow(++rowNo);

			// setting value in that row
			row.createCell(0).setCellValue(classHour.getClassHourId());
			row.createCell(1).setCellValue(dateTimeFormatter.format(classHour.getBeginsAt()));
			row.createCell(2).setCellValue(dateTimeFormatter.format(classHour.getEndsAt()));
//			row.createCell(3).setCellValue(classHour.getSubject().getSubjectName());
//			row.createCell(4).setCellValue(classHour.getUser().getFirstName());
//			row.createCell(5).setCellValue(classHour.getRoomNo());

			if (classHour.getSubject() == null) {
				row.createCell(3).setCellValue("");
			} else {
				row.createCell(3).setCellValue(classHour.getSubject().getSubjectName());
			}

			if (classHour.getUser() == null) {
				row.createCell(4).setCellValue("");
			} else {
				row.createCell(4).setCellValue(classHour.getUser().getUsername());
			}

			try {
				workbook.write(new FileOutputStream(excelRequest.getFilePath() + "\\text.xlsx"));
			} catch (Exception e) {

				e.printStackTrace();
			}

		}

		return null;
	}

	@Override
	public ResponseEntity<?> writeToExcel(MultipartFile file, int programId, LocalDate fromDate, LocalDate toDate)
			throws IOException {

		AcademicProgram academicProgram = academicProgramRepo.findById(programId).orElseThrow(
				() -> new UserNotFoundException("No user With This Id", HttpStatus.NOT_FOUND, "No User In DatBAse"));
		List<ClassHour> classhours = classHourRepo.findByAcademicProgramAndBeginsAtBetween(academicProgram, fromDate,
				toDate);

		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH-mm");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd");
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

		workbook.forEach(sheet -> {

			int rowNo = 0;
			Row row = sheet.createRow(rowNo);
			row.createCell(0).setCellValue("Class Hour Id");
			row.createCell(1).setCellValue("from");
			row.createCell(2).setCellValue("to");
			row.createCell(3).setCellValue("subject");
			row.createCell(4).setCellValue("user");
			row.createCell(5).setCellValue("room no");

			for (ClassHour classHour : classhours) {
				Row row2 = sheet.createRow(++rowNo);

				// setting value in that row
				row2.createCell(0).setCellValue(classHour.getClassHourId());
				row2.createCell(1).setCellValue(dateTimeFormatter.format(classHour.getBeginsAt()));
				row2.createCell(2).setCellValue(dateTimeFormatter.format(classHour.getEndsAt()));
//				row.createCell(3).setCellValue(classHour.getSubject().getSubjectName());
//				row.createCell(4).setCellValue(classHour.getUser().getFirstName());
//				row.createCell(5).setCellValue(classHour.getRoomNo());

				if (classHour.getSubject() == null) {
					row2.createCell(3).setCellValue("");
				} else {
					row2.createCell(3).setCellValue(classHour.getSubject().getSubjectName());
				}

				if (classHour.getUser() == null) {
					row2.createCell(4).setCellValue("");
				} else {
					row2.createCell(4).setCellValue(classHour.getUser().getUsername());
				}
			}
		});

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		byte[] byteData = outputStream.toByteArray();

		return ResponseEntity.ok().header("Content Disposition", "attachment filename" + file.getOriginalFilename())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).
				body(byteData);
	}
}
