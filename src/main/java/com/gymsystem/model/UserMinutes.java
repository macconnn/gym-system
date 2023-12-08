package com.gymsystem.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "user_minutes")
public class UserMinutes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int license_id;

    int user_id;
    int license;
}
