package com.liyifu.clothesojbackend.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建题目请求
 */

@Data
public class QuestionAddRequest implements Serializable {
    private String title;

    private String content;

    private List<String> tags;

    private String answer;

    private List<JudgeCase> judgeCase;

    private JudgeConfig judgeConfig;

}
