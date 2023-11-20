package com.liyifu.clothesojbackend.model.dto.questionsubmit;

import com.liyifu.clothesojbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    private Long userId;

    private Long questionId;

    private String submitLanguage;

    private Integer status;
}
