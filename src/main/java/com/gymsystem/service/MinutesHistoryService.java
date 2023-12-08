package com.gymsystem.service;

import com.gymsystem.model.MinutesHistory;
import com.gymsystem.repository.MinutesHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MinutesHistoryService {

    @Autowired
    MinutesHistoryRepository minutesHistoryRepository;

    public void insertAddLicenseLog(MinutesHistory minutesHistory){
        minutesHistoryRepository.save(minutesHistory);
    }



}
