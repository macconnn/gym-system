package com.gymsystem.controller;

import com.gymsystem.dto.AddMinutesReq;
import com.gymsystem.dto.ErrorRes;
import com.gymsystem.dto.NormalRes;
import com.gymsystem.dto.SingUpReq;
import com.gymsystem.model.MinutesHistory;
import com.gymsystem.model.Users;
import com.gymsystem.service.MinutesHistoryService;
import com.gymsystem.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    @Autowired
    UsersService usersService;
    @Autowired
    MinutesHistoryService minutesHistoryService;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<List<Users>> getAllUsers(){
        return ResponseEntity.ok(usersService.readAllUsersInfo());
    }

    @RequestMapping(value = "/singup", method = RequestMethod.POST)
    public ResponseEntity singUp(@RequestBody SingUpReq singUpReq){
        String emailFromDB = usersService.readEmailByEmail(singUpReq.getEmail());
        if(emailFromDB != null){
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, "The email already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        Users users = usersService.saveSingUpUser(singUpReq);
        if(users == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else {
            return ResponseEntity.ok(users);
        }
    }

    @RequestMapping(value = "/minutes", method = RequestMethod.POST)
    public ResponseEntity addLicense(@RequestBody AddMinutesReq addMinutesReq){
        MinutesHistory minutesHistory = new MinutesHistory();
        Integer user_id = usersService.findUseridByName(addMinutesReq.getName());

        if(user_id == null){
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, "Select name not exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        minutesHistory.setUser_id(user_id);
        minutesHistory.setMinutes(addMinutesReq.getMinutes());
        minutesHistoryService.insertAddLicenseLog(minutesHistory);
        NormalRes normalRes = new NormalRes(HttpStatus.OK, "Add minutes OK");
        return ResponseEntity.status(HttpStatus.OK).body(normalRes);
    }

}
