package com.school.sba.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.ConstraintViolationException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.repository.ClassHourRepo;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private ClassHourRepo classHourRepo;

	@Autowired
	private UserRepo userRepo;

	public User mapToUser(UserRequest userRequest) {
		return User.builder().username(userRequest.getUsername())
				.password(passwordEncoder.encode(userRequest.getPassword())).firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName()).contactNo(userRequest.getContactNo()).email(userRequest.getEmail())
				.userRole(userRequest.getUserRole()).build();
	}

	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().username(user.getUsername()).firstName(user.getFirstName())
				.lastName(user.getLastName()).email(user.getEmail()).userRole(user.getUserRole()).build();
	}

	@Override
	public Object registerUser(UserRequest userRequest) {
		User user2 = userRepo.findByUserRole(UserRole.ADMIN).get();
		User user = mapToUser(userRequest);
		user.setDeleted(false);
		// adding school through Admin
		user.setSchool(user2.getSchool());
		// Admin which is present in database through that we are setting in to
		// school,means there should not be again Admin
		if (user.getUserRole() != UserRole.ADMIN) {
			try {
				user = userRepo.save(user);
			} catch (Exception e) {
				throw new ConstraintViolationException("Duplicate Entry made", HttpStatus.IM_USED,
						"No duplicate entries are allowed");
			}
		} else {
			throw new UserNotFoundException("User with given ID cannot be registered as admin in the database",
					HttpStatus.NOT_FOUND, "Admin is already present in database");
		}
		return "User Saved Successfully";
	}

	@Override
	public Object deleteUser(int userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User with given ID is not registered in the database",
						HttpStatus.NOT_FOUND, "No such user in database"));
		user.setDeleted(true);
		userRepo.delete(user);
		return "User Deleted Successfully";
	}

	@Override
	public UserResponse findUserById(int userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User with given ID is not registered in the database",
						HttpStatus.NOT_FOUND, "No such user in database"));
		return mapToUserResponse(user);
	}

	@Override
	public Object registerAdmin(UserRequest userRequest) {
		User user = mapToUser(userRequest);
		user.setDeleted(false);
		if (!(userRepo.existsByUserRole(UserRole.ADMIN)) && user.getUserRole() == UserRole.ADMIN) {
			try {
				user = userRepo.save(user);
			} catch (Exception e) {
				throw new ConstraintViolationException("Duplicate Entry made", HttpStatus.IM_USED,
						"No duplicate entries are allowed");
			}
		} else {
			throw new ConstraintViolationException("There is already an admin", HttpStatus.IM_USED,
					"More than 1 admin is not allowed");
		}
		return "User Saved Successfully";
	}

//	@Override
//	public Object addUser(UserRequest userRequest) {
//		User user2 = userRepo.findByUserRole(UserRole.ADMIN).get();
//		User user =mapToUser(userRequest);
//		
//		return null;
//	}

	@Override
	public String deleteUser() {
		List<User> users = userRepo.findByIsDeleted(true);
		users.forEach((b) -> {
			if (!(b.getUserRole().equals(UserRole.ADMIN))) {
				b.setDeleted(true);
				
				//bcz User was connected to AcademicProgram so we cannot delete it directly , so first we have to remove that connected one  
				b.setAcademicPrograms(null);

				List<ClassHour> hour = classHourRepo.findByUser(b);
				hour.forEach((cl) -> {
					
					//removing connection to that Users
					cl.setUser(null);
					
					classHourRepo.save(cl);
				});
				
				//bcz first we removed and that we have to save first that is why we are doing this
				userRepo.save(b);
				userRepo.delete(b);
			}
		});

		return "User has been Deleted";
	}

}
