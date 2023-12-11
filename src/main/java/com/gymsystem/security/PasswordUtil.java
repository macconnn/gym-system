package com.gymsystem.security;

import com.gymsystem.dto.LoginRequest;
import com.gymsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    @Autowired
    UserService userService;

    public String validPassword(LoginRequest loginRequest){
        String passwordByEmail = userService.findPasswordByEmail(loginRequest.getEmail());
        boolean valid = bCryptValidation(loginRequest.getPassword(), passwordByEmail);
        if(valid){
            return passwordByEmail;
        }else {
            return null;
        }
    }

    private boolean bCryptValidation(String input, String fromDB){
        return BCrypt.checkpw(input, fromDB);
    }



}
