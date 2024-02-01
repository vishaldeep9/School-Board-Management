package com.school.sba.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.service.AcademicProgramService;
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
	
	@Scheduled(fixedDelay = 1000L)
	public void test() {
	
		
		String deleteUser = userService.deleteUser();
		log.info(deleteUser);

		log.info(programService.deleteAcademic());
		

	}

}
