package com.gymsystem.service.imp;

import com.gymsystem.model.MinutesHistory;
import com.gymsystem.model.UserMinutes;
import com.gymsystem.repository.UserMinutesRepository;
import com.gymsystem.service.MinutesHistoryService;
import com.gymsystem.service.UserMinutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserMinutesServiceImp implements UserMinutesService {

    @Autowired
    UserMinutesRepository userMinutesRepository;
    @Autowired
    MinutesHistoryService  minutesHistoryService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public UserMinutes updateUserMinutes(UserMinutes userMinutesInput) {
        UserMinutes userMinutes = userMinutesRepository.findAllByUserId(userMinutesInput.getUserId());
        if(userMinutes == null){
            userMinutesRepository.save(userMinutesInput);  // first time add minutes
        }else{
            userMinutes.setMinutes(userMinutes.getMinutes() + userMinutesInput.getMinutes());
            userMinutesRepository.save(userMinutes);
        }

        MinutesHistory minutesHistory = new MinutesHistory();
        minutesHistory.setUserId(userMinutesInput.getUserId());
        minutesHistory.setMinutes(userMinutesInput.getMinutes());
        minutesHistoryService.insertAddMinutesLog(minutesHistory);

        return userMinutesRepository.findAllByUserId(userMinutesInput.getUserId());
    }

}
