package com.liyifu.clothesojbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDeleteRequest implements Serializable {
    private Long id;
}
