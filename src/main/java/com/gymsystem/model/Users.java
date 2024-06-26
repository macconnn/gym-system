package com.gymsystem.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "users")
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int user_id;

    @Column(name = "email")
    String email;
    @Column(name = "password")
    String password;
    @Column(name = "name")
    String name;
    @Column(name = "nid")
    String nid;
    @Column(name = "role")
    String role;
    @Column(name = "birth")
    String birth;
    @Column(name = "phone")
    String phone;
    @Column(name = "address")
    String address;
}
