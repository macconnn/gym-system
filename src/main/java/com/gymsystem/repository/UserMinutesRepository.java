package com.gymsystem.repository;

import com.gymsystem.model.UserMinutes;
import org.springframework.data.repository.CrudRepository;

public interface UserMinutesRepository extends CrudRepository<UserMinutes, Long> {
    UserMinutes findAllByUserId(int user_id);
}
