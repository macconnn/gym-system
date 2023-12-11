package com.gymsystem.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "user_minutes")
public class UserMinutes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "license_id")
    int license_id;
    @Column(name = "user_id")
    int userId;
    @Column(name = "minutes")
    int minutes;
}
