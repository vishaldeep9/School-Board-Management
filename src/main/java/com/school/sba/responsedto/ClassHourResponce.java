package com.school.sba.responsedto;

import java.time.LocalTime;

import com.school.sba.enums.ClassStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassHourResponce {

	 private LocalTime beginsAt;
	 private LocalTime endsAt;
	 private int roomNo;
	 private ClassStatus classStatus;
}
