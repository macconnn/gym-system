package com.gymsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SingUpReq {
    String email;
    String password;
    String name;
    String nid;
    String role;
    String birth;
    String phone;
    String address;
}
