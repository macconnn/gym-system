package com.gymsystem.repository;

import com.gymsystem.model.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsersRepository extends CrudRepository<Users, Long> {
    Users findAllByEmail(String email);
    Users findAllByEmailAndNid(String email, String nid);
    Users findAllByName(String name);
    List<Users> findAll();
    @Query(value = "SELECT u.role FROM users u WHERE u.email = :email")
    String findRoleByEmail(String email);
    @Query("SELECT u.password FROM users u WHERE u.email = :email")
    String findPasswordByEmail(String email);
    @Query(value = "SELECT u.email FROM users u WHERE u.email = :email")
    String findEmailByEmail(String email);
    @Query(value = "SELECT u.user_id FROM users u WHERE u.name = :name")
    Integer findUseridByName(String name);
    @Query(value = "SELECT u.name FROM users u WHERE u.email = :email")
    String findNameByEmail(String email);
}
