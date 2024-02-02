package com.school.sba.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.service.ClassHourService;
import com.school.sba.service.UserService;
import com.school.sba.serviceImpl.AcademicProgramServiceImpl;
import com.school.sba.serviceImpl.UserServiceImpl;

import io.swagger.v3.oas.annotations.info.Info;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@NoArgsConstructor
@Slf4j
public class ScheduleJobs {

	@Autowired
	private UserService userService;

	@Autowired
	private AcademicProgramService programService;

	@Autowired
	private ClassHourService classHourService;

	@Autowired
	private AcademicProgramRepo programRepo;

	@Scheduled(fixedDelay = 1000L)
	public void test() {

		String deleteUser = userService.deleteUser();
		log.info(deleteUser);

		log.info(programService.deleteAcademic());
	}

	// <minute> <hour> <day-of-month> <month> <day-of-week>
	@Scheduled(cron = "* * * * MON")
	public void generateClassHourForMonday() {

		List<AcademicProgram> findAll = programRepo.findAll();
		findAll.forEach((ac) -> {
			if (ac.isAutoRepeat()) {
				classHourService.createClassHourForNextWeek(ac.getProgramId());
			}
		});

	}

}
