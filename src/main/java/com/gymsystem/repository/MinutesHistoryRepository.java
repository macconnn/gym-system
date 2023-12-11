package com.gymsystem.repository;

import com.gymsystem.model.MinutesHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MinutesHistoryRepository extends CrudRepository<MinutesHistory, Long> {
    List<MinutesHistory> findAllByUserId(int userId);
}
