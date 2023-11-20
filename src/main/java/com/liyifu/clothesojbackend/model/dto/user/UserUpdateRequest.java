package com.liyifu.clothesojbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable {
    private Long id;

    private String username;

    private String userRole;
}
