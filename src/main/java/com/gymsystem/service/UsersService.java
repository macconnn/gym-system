package com.gymsystem.service;

import com.gymsystem.dto.ChangePwdReq;
import com.gymsystem.dto.LoginReq;
import com.gymsystem.dto.SingUpReq;
import com.gymsystem.model.Users;
import com.gymsystem.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsersService {
    @Autowired
    UsersRepository usersRepository;

    public Users readUserInfoByEmail(String email){
        Users users = usersRepository.findAllByEmail(email);
        if(users == null){
            System.out.println("cannot get user");
        }
        return users;
    }

    public List<Users> readAllUsersInfo(){
        return usersRepository.findAll();
    }

    public String readRoleByEmail(String email){
        return usersRepository.findRoleByEmail(email);
    }

    public String readEmailByEmail(String email){
        return usersRepository.findEmailByEmail(email);
    }

    @Transactional
    public Users updateUserPasswordByEmailAndNationID(ChangePwdReq changePwdReq){
        Users users = usersRepository.findAllByEmailAndNid(changePwdReq.getEmail(), changePwdReq.getNid());
        if(users != null){
            users.setPassword(passwordToHash(changePwdReq.getNewPassword()));
            usersRepository.save(users);
        }
        return users;
    }

    public Users saveSingUpUser(SingUpReq singUpReq){
        try {
            Users users = new Users();
            users.setEmail(singUpReq.getEmail());
            users.setPassword(passwordToHash(singUpReq.getPassword()));
            users.setName(singUpReq.getName());
            users.setNid(singUpReq.getNid());
            users.setRole(singUpReq.getRole());
            users.setBirth(singUpReq.getBirth());
            users.setPhone(singUpReq.getPhone());
            users.setAddress(singUpReq.getAddress());
            return usersRepository.save(users);
        }catch (Exception e){
            System.out.println("Sing up failed");
            e.printStackTrace();
            return null;
        }
    }

    public String passwordToHash(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public String validPassword(LoginReq loginReq){
        String passwordByEmail = usersRepository.findPasswordByEmail(loginReq.getEmail());
        boolean valid = bCryptValidation(loginReq.getPassword(), passwordByEmail);
        if(valid){
            return passwordByEmail;
        }else {
            return null;
        }
    }

    public Integer findUseridByName(String name){
        return usersRepository.findUseridByName(name);
    }

    private boolean bCryptValidation(String input, String fromDB){
        return BCrypt.checkpw(input, fromDB);
    }

}
