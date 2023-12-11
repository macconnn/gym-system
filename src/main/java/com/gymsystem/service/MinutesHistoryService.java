package com.gymsystem.service;

import com.gymsystem.model.MinutesHistory;

import java.util.List;

public interface MinutesHistoryService {
    void insertAddMinutesLog(MinutesHistory minutesHistory);
    List<MinutesHistory> findAllByUserId(int userId);
}
