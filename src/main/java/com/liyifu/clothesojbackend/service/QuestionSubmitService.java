package com.liyifu.clothesojbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyifu.clothesojbackend.model.dto.question.QuestionQueryRequest;
import com.liyifu.clothesojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.liyifu.clothesojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.liyifu.clothesojbackend.model.entity.QuestionSubmit;
import com.liyifu.clothesojbackend.model.entity.User;
import com.liyifu.clothesojbackend.model.vo.QuestionSubmitVO;

/**
* @author liyifu
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-11-14 01:34:16
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 提交题目
     */
    Long doSubmitQuestion(QuestionSubmitAddRequest questionSubmitRequest, User loginUser);

    /**
     * 获取条件构造器
     */
    QueryWrapper<QuestionSubmit> getQuestionSubmitQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取脱敏后的题目提交信息
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit,User loginUser);

    /**
     * 分页获取脱敏后的题目提交信息
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage,User loginUser);
}
