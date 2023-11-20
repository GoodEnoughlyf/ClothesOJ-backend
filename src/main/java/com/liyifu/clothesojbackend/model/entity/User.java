package com.liyifu.clothesojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName user用户
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     * @TableId(type = IdType.ASSIGN_ID)表示使用雪花算法自动生成主键ID，因此生成的id都是bigint，防止别人用爬虫按顺序爬取数据
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户角色  （user、admin、ban）
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否被删除
     * @TableLogic注解表示逻辑删除 ，是mybatis-plus中的，在配置文件中绑定每一个表的isDelete
     */
    @TableLogic
    private Integer isDelete;

    /**
     * @TableField(exist = false)注解表示 这个字段不是数据库中的字段，而是自己添加的字段。
     * https://blog.csdn.net/qq_33331448/article/details/120536274  （链接为解释serialVersionUID = 1L是什么？）
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}