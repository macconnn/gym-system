package com.gymsystem.service;

import com.gymsystem.dto.ChangePwdRequest;
import com.gymsystem.dto.SingUpRequest;
import com.gymsystem.model.Users;

import java.util.List;

public interface UserService {
    Users findAllByEmail(String email);
    Users findAllByName(String name);
    List<Users> findAllUsers();
    String findRoleByEmail(String email);
    String findPasswordByEmail(String email);
    String findEmailByEmail(String email);
    String findNameByEmail(String email);
    Integer findUseridByName(String name);
    Users updateUserPasswordByEmailAndNationID(ChangePwdRequest changePwdRequest);
    Users saveSingUpUser(SingUpRequest singUpRequest);
}
