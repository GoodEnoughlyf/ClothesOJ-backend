package com.liyifu.clothesojbackend.judge;

import com.liyifu.clothesojbackend.judge.codesandbox.model.JudgeInfo;
import com.liyifu.clothesojbackend.judge.strategy.*;
import com.liyifu.clothesojbackend.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题策略管理
 *
 * 如果选择某种判题策略的过程比较复杂，如果都写在调用判题服务judgeServiceImpl的代码中，代码会越来越复杂，会有大量 if ... else ...，所以建议单独编写一个判断策略的类。
 *
 * 目的是尽量简化对判题功能的调用，让调用方写最少的代码、调用最简单。对于判题策略的选取，也是在 JudgeManager 里处理的。
 */
@Service
public class JudgeManager {
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String submitLanguage = questionSubmit.getSubmitLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if("java".equals(submitLanguage)){
            judgeStrategy= new JavaJudgeStrategy();
        } else if ("go".equals(submitLanguage)) {
            judgeStrategy=new GoJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
