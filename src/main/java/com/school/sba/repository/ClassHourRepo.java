package com.school.sba.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.User;

public interface ClassHourRepo extends JpaRepository<ClassHour, Integer> {

	boolean existsByBeginsAtIsLessThanEqualAndEndsAtIsGreaterThanEqualAndRoomNo(LocalDateTime beginsAt,
			LocalDateTime endsAt, int roomNo);

	
	
	boolean existsByBeginsAtBetweenAndRoomNo(LocalDateTime beginsAt, LocalDateTime endsAt,int roomNo );



	List<ClassHour> findByUser(User b);
	
	
}
