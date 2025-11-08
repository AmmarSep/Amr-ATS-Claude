package com.spring.getready.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer userId;
    private String username;
    private String email;
    private String userUuid;
    private Timestamp createdOn;
    private Timestamp lastLoginOn;
    private Boolean isLocked;
    private String role;
    private String groupName;
}
