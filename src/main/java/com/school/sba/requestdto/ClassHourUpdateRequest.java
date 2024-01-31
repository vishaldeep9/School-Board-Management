package com.school.sba.requestdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter	
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassHourUpdateRequest {

	private int classHourId;	
	 private int subjectId;
	 private int userId;	
	 private int roomNo;
}	
