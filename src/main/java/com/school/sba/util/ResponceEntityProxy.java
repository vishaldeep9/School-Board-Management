package com.school.sba.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponceEntityProxy {
	public static <T> ResponseEntity<ResponseStructure<T>> getResponseEntity(HttpStatus status, String message, T data)
	{
		ResponseStructure<T> responseStructure = new ResponseStructure<T>();
		
		responseStructure.setStatus(status.value());
		responseStructure.setMessage(message);
		responseStructure.setData(data);
		
		return new ResponseEntity<ResponseStructure<T>>(responseStructure,status);
	}
}
