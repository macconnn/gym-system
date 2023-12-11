package com.gymsystem.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "minutes_history")
public class MinutesHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mh_id")
    int mh_id;
    @Column(name = "user_id")
    int userId;
    @Column(name = "minutes")
    int minutes;
}
