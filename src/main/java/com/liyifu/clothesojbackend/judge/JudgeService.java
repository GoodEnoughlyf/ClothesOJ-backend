package com.liyifu.clothesojbackend.judge;

import com.liyifu.clothesojbackend.model.entity.QuestionSubmit;

/**
 * 判题服务  ：执行代码
 */
public interface JudgeService {

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(Long questionSubmitId);
}
