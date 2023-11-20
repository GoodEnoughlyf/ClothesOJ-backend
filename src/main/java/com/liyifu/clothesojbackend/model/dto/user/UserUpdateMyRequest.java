package com.liyifu.clothesojbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改个人信息请求
 */
@Data
public class UserUpdateMyRequest implements Serializable {
    private String username;
}
