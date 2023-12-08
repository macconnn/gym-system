package com.gymsystem.controller;

import com.gymsystem.dto.*;
import com.gymsystem.model.*;
import com.gymsystem.security.JwtUtil;
import com.gymsystem.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UsersService usersService;
    private final AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginReq loginReq) {
        String bCryptCheck = usersService.validPassword(loginReq);

        if(bCryptCheck == null){
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            loginReq.getEmail(), bCryptCheck)); // bCryptCheck is bCrypt password from DB in first sing up

            String email = authentication.getName();
            String token = jwtUtil.createToken(loginReq);
            LoginRes loginRes = new LoginRes(email, token);

            return ResponseEntity.ok(loginRes);

        } catch (BadCredentialsException e) {
            e.printStackTrace();
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public ResponseEntity userChangePassword(@RequestBody ChangePwdReq changePwdReq){
        String emailFromDB = usersService.readEmailByEmail(changePwdReq.getEmail());
        if(emailFromDB == null){
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, "The email not correct");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return ResponseEntity.status(HttpStatus.OK).body(usersService.updateUserPasswordByEmailAndNationID(changePwdReq));
    }

    // this is for development remove when done
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

}
