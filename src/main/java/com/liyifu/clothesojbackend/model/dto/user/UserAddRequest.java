package com.liyifu.clothesojbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员创建用户请求体
 */
@Data
public class UserAddRequest implements Serializable {

    private String username;

    private String userAccount;

    private String userRole;

}
