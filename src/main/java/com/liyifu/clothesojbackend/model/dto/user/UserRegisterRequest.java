package com.liyifu.clothesojbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {
    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
