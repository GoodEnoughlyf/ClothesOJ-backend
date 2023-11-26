package com.liyifu.clothesojbackend.judge;

import com.liyifu.clothesojbackend.common.ErrorCode;
import com.liyifu.clothesojbackend.exception.BusinessException;
import com.liyifu.clothesojbackend.model.entity.Question;
import com.liyifu.clothesojbackend.model.entity.QuestionSubmit;
import com.liyifu.clothesojbackend.service.QuestionService;
import com.liyifu.clothesojbackend.service.QuestionSubmitService;

import javax.annotation.Resource;

public class JudgeServiceImpl implements  JudgeService{
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Override
    public QuestionSubmit doJudge(Long questionSubmitId) {
        //1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if(questionSubmit==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"提交信息不存在!");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if(question==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目信息不存在！");
        }

        //2）如果题目提交状态不为等待中，就不用重复执行了

        //3）更改判题（题目提交）的状态为 “判题中”，防止重复执行，也能让用户即时看到状态

        //4）调用沙箱，获取到执行结果

        //5）根据沙箱的执行结果，设置题目的判题状态和信息
        return null;
    }
}
