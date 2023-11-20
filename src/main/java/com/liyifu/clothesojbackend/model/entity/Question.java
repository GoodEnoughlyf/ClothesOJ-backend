package com.liyifu.clothesojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     * 数据库中用 json数组 格式的字符串存放，便于前端转成数组展示
     * 即 “["haha","jeje"]”
     */
    private String tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 判题用例
     * 数据库中用 json数组 格式的字符串存放，便于前端转成数组展示
     */
    private String judgeCase;

    /**
     * 判题配置
     * 数据库中用 json数组 格式的字符串存放，便于前端转成数组展示
     */
    private String judgeConfig;

    /**
     * 题目提交次数
     */
    private Integer submitNum;

    /**
     * 题目通过次数
     */
    private Integer acceptedNum;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}