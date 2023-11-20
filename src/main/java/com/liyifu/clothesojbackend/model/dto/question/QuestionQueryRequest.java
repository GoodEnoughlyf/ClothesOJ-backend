package com.liyifu.clothesojbackend.model.dto.question;

import com.liyifu.clothesojbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private List<String> tags;

    private String answer;
}
