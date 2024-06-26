# gym-system

## description
This system records how much time each gym member spends during each training  
the administrator has permission to plus/minus every member's time value

## dependency
1. MySQL
2. redis
3. jdk 1.8
4. maven

## function available
1. plus/minus member time value(only admin role)
2. find all and specific member information
3. log in and asign jwt for user
4. spring security and jwt is available

## note
I don't have front-end view to show, if you're interesting in this project send PR to me


## MySQL schema
```
CREATE TABLE IF NOT EXISTS `users` (
    `user_id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL,
    `email` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `nid` char(10)  NOT NULL,
    `role` char(8) NOT NULL,
    `birth` char(9) NOT NULL,
    `phone` char(10) NOT NULL,
    `address` varchar(255) NOT NULL,
    PRIMARY KEY (`user_id`),
    INDEX idx_name_email (name, email),
    UNIQUE(`email`),
    UNIQUE(`nid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `minutes_history` (
    `mh_id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `minutes` int NOT NULL,
    `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`mh_id`),
    FOREIGN KEY(`user_id`) REFERENCES users(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `user_minutes` (
    `license_id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `minutes` int NOT NULL,
    PRIMARY KEY (`license_id`),
    FOREIGN KEY(`user_id`) REFERENCES users(`user_id`),
    UNIQUE(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```
