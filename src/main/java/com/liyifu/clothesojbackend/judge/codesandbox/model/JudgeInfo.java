package com.liyifu.clothesojbackend.judge.codesandbox.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class JudgeInfo{
    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 内存消耗
     */
    private Long memory;

    /**
     * 时间消耗
     */
    private Long time;
}
