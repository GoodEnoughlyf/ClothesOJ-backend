package com.liyifu.clothesojbackend.model.dto.user;

import com.liyifu.clothesojbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 管理员利用mybatis-plus分页获取用户列表请求   （继承了分页请求）
 *
 * @EqualsAndHashCode(callSuper = true) 不加该注解的影响：子类对象属性值一致，但其继承的父类对象属性值不一致，在比较的时候会出现比较结果不对的情况。
 * https://blog.csdn.net/weixin_44536553/article/details/118180163  (具体解释)
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    private Long id;

    private String username;

    private String userRole;
}
