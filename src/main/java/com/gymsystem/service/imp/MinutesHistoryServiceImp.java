package com.gymsystem.service.imp;

import com.gymsystem.model.MinutesHistory;
import com.gymsystem.repository.MinutesHistoryRepository;
import com.gymsystem.service.MinutesHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MinutesHistoryServiceImp implements MinutesHistoryService {

    @Autowired
    MinutesHistoryRepository minutesHistoryRepository;

    public List<MinutesHistory> findAllByUserId(int userId){
        return minutesHistoryRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void insertAddMinutesLog(MinutesHistory minutesHistory){
        minutesHistoryRepository.save(minutesHistory);
    }



}
