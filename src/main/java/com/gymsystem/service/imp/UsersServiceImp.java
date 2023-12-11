package com.gymsystem.service.imp;

import com.gymsystem.dto.ChangePwdRequest;
import com.gymsystem.dto.SingUpRequest;
import com.gymsystem.model.Users;
import com.gymsystem.repository.UsersRepository;
import com.gymsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class UsersServiceImp implements UserService {
    @Autowired
    UsersRepository usersRepository;

    public Users findAllByEmail(String email){
        Users users = usersRepository.findAllByEmail(email);
        if(users == null){
            System.out.println("cannot get user");
        }
        return users;
    }

    public Users findAllByName(String name){
        return usersRepository.findAllByName(name);
    }

    public List<Users> findAllUsers(){
        return usersRepository.findAll();
    }

    public String findRoleByEmail(String email){
        return usersRepository.findRoleByEmail(email);
    }

    public String findPasswordByEmail(String email){
        return usersRepository.findPasswordByEmail(email);
    }

    public String findEmailByEmail(String email){
        return usersRepository.findEmailByEmail(email);
    }

    public String findNameByEmail(String email){
        return usersRepository.findNameByEmail(email);
    }

    public Integer findUseridByName(String name){
        return usersRepository.findUseridByName(name);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Users updateUserPasswordByEmailAndNationID(ChangePwdRequest changePwdRequest){
        Users users = usersRepository.findAllByEmailAndNid(changePwdRequest.getEmail(), changePwdRequest.getNid());
        if(users != null){
            users.setPassword(passwordToHash(changePwdRequest.getNewPassword()));
            usersRepository.save(users);
        }
        return users;
    }

    public Users saveSingUpUser(SingUpRequest singUpRequest){
        try {
            Users users = new Users();
            users.setEmail(singUpRequest.getEmail());
            users.setPassword(passwordToHash(singUpRequest.getPassword()));
            users.setName(singUpRequest.getName());
            users.setNid(singUpRequest.getNid());
            users.setRole(singUpRequest.getRole());
            users.setBirth(singUpRequest.getBirth());
            users.setPhone(singUpRequest.getPhone());
            users.setAddress(singUpRequest.getAddress());
            return usersRepository.save(users);
        }catch (Exception e){
            System.out.println("Sing up failed");
            e.printStackTrace();
            return null;
        }
    }

    private String passwordToHash(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }




}
