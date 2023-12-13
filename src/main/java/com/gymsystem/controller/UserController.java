package com.gymsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymsystem.dto.*;
import com.gymsystem.model.MinutesHistory;
import com.gymsystem.model.UserMinutes;
import com.gymsystem.model.Users;
import com.gymsystem.security.JwtUtil;
import com.gymsystem.security.PasswordUtil;
import com.gymsystem.service.MinutesHistoryService;
import com.gymsystem.service.UserMinutesService;
import com.gymsystem.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    MinutesHistoryService minutesHistoryService;
    @Autowired
    UserMinutesService userMinutesService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired(required = false)
    RedisTemplate redisTemplate;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    PasswordUtil passwordUtil;
    private final long CACHE_KEY_TIMEOUT = 60;

    @RequestMapping(value = "/users/login", method = RequestMethod.GET)
    public ModelAndView getLoginPage(){
        return new ModelAndView("login", HttpStatus.OK);
    }
    @RequestMapping(value = "/users/logout", method = RequestMethod.GET)
    public String getLogoutPage(){
        return "login";
    }
    @RequestMapping(value = "/users/login", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity postLogin(@RequestBody LoginRequest loginRequest) {
        String bCryptCheck = passwordUtil.validPassword(loginRequest);

        if(bCryptCheck == null){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), bCryptCheck)); // bCryptCheck is bCrypt password from DB in first sing up

            String email = authentication.getName();
            String name = userService.findNameByEmail(email);
            String token = jwtUtil.createToken(loginRequest);
            LoginResponse loginResponse = new LoginResponse(name, email, token);
            return ResponseEntity.status(HttpStatus.OK).body(loginResponse);

        } catch (BadCredentialsException e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @RequestMapping(value = "/users/changePassword", method = RequestMethod.PUT)
    public ResponseEntity userChangePassword(@RequestBody ChangePwdRequest changePwdRequest){
        String emailFromDB = userService.findEmailByEmail(changePwdRequest.getEmail());
        if(emailFromDB == null){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "The email not correct");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserPasswordByEmailAndNationID(changePwdRequest));
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<Users>> getAllUsers(){
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity singUp(@RequestBody SingUpRequest singUpRequest){
        String emailFromDB = userService.findEmailByEmail(singUpRequest.getEmail());
        if(emailFromDB != null){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "The email already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        Users users = userService.saveSingUpUser(singUpRequest);
        if(users == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else {
            return ResponseEntity.ok(users);
        }
    }

    @RequestMapping(value = "/users/{name}", method = RequestMethod.GET)
    public ResponseEntity userInfoByName(HttpServletRequest request, @PathVariable String name){
        Claims claims = jwtUtil.resolveClaims(request);
        String email = jwtUtil.getEmail(claims);
        Users users = (Users) redisTemplate.opsForValue().get("users_" + email);

        // write users to redis
        if(users == null){
            users = userService.findAllByEmail(email);
            redisTemplate.opsForValue().set("users_" + email, users, CACHE_KEY_TIMEOUT, TimeUnit.SECONDS);
        }

        if(users.getName().equals(name)){
            return ResponseEntity.status(HttpStatus.OK).body(users);
        }else if(users.getName().equals("Admin")){
            users = userService.findAllByName(name);
            return ResponseEntity.status(HttpStatus.OK).body(users);
        }else {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "The name not correct");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @RequestMapping(value = "/users/{name}/history", method = RequestMethod.GET)
    public ResponseEntity userHistory(HttpServletRequest request, @PathVariable String name){
        Claims claims = jwtUtil.resolveClaims(request);
        String email = jwtUtil.getEmail(claims);
        String nameFromEmail = (String) redisTemplate.opsForValue().get("email_" + email);

        // write users to redis
        if(nameFromEmail == null){
            nameFromEmail = userService.findNameByEmail(email);
            redisTemplate.opsForValue().set("email_" + email, nameFromEmail, CACHE_KEY_TIMEOUT, TimeUnit.SECONDS);
        }

        if(nameFromEmail.equals(name) || nameFromEmail.equals("Admin")){
            Integer userId = userService.findUseridByName(name);
            List<MinutesHistory> minutesHistories = minutesHistoryService.findAllByUserId(userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(minutesHistories);
        }else {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "The name not correct");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @RequestMapping(value = "/users/minutes", method = RequestMethod.POST)
    public ResponseEntity addMinutes(@RequestBody MinutesActRequest minutesActRequest){
        UserMinutes userMinutesInput = new UserMinutes();
        UserMinutes userMinutes = null;
        MinutesActResponse minutesActResponse = new MinutesActResponse();
        Integer user_id = userService.findUseridByName(minutesActRequest.getName());

        if(user_id == null){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Select name not exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            // update user minutes
            userMinutesInput.setUserId(user_id);
            userMinutesInput.setMinutes(minutesActRequest.getMinutes());
            userMinutes = userMinutesService.updateUserMinutes(userMinutesInput);
            minutesActResponse.setMessage("Update minutes OK");
            minutesActResponse.setMinutes(userMinutes.getMinutes());
        }catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Update user minutes failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(minutesActResponse);
    }

    @RequestMapping(value = "/users/minutes", method = RequestMethod.DELETE)
    public ResponseEntity reduceMinutes(@RequestBody MinutesActRequest minutesActRequest){
        UserMinutes userMinutesInput = new UserMinutes();
        UserMinutes userMinutes = null;
        MinutesActResponse minutesActResponse = new MinutesActResponse();
        Integer user_id = userService.findUseridByName(minutesActRequest.getName());

        if(user_id == null){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Select name not exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            // update user minutes
            userMinutesInput.setUserId(user_id);
            userMinutesInput.setMinutes(-minutesActRequest.getMinutes());
            userMinutes = userMinutesService.updateUserMinutes(userMinutesInput);
            minutesActResponse.setMessage("Update minutes OK");
            minutesActResponse.setMinutes(userMinutes.getMinutes());
        }catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Update user minutes failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(minutesActResponse);
    }

}
