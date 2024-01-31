package com.school.sba.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.serviceImpl.AcademicProgramServiceImpl;
import com.school.sba.serviceImpl.UserServiceImpl;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class ScheduleJobs {

	@Scheduled(fixedDelay = 1000L)
	public void test() {
//		System.out.println("Scheduled Jobs");
		
		
       UserServiceImpl impl=new UserServiceImpl();
       impl.deleteUser();
       
       AcademicProgramServiceImpl ac= new AcademicProgramServiceImpl();
       ac.deleteAcademic();
	}

}
