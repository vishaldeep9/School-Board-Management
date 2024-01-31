package com.school.sba.service;

import com.school.sba.requestdto.UserRequest;

public interface UserService {

	Object registerAdmin(UserRequest userRequest);

	Object deleteUser(int userId);

	Object findUserById(int userId);

	Object registerUser(UserRequest userRequest);

	String deleteUser();

//	Object addUser(UserRequest userRequest);

}
