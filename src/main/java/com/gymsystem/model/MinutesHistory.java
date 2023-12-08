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
@Entity(name = "minutes_history")
public class MinutesHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int mh_id;

    int user_id;
    int minutes;
}
