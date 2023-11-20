package com.liyifu.clothesojbackend.model.dto.questionsubmit;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionSubmitAddRequest implements Serializable {
    private Long questionId;

    private String submitLanguage;

    private String submitCode;
}
