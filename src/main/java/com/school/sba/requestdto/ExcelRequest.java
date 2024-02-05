package com.school.sba.requestdto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExcelRequest {

	private LocalDate fromDate;
	private LocalDate toDate;
	private String filePath;
}
