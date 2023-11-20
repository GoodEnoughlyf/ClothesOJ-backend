package com.liyifu.clothesojbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyifu.clothesojbackend.model.dto.question.QuestionQueryRequest;
import com.liyifu.clothesojbackend.model.entity.Question;
import com.liyifu.clothesojbackend.model.vo.QuestionVO;

import java.util.List;

/**
 * @author liyifu
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2023-11-14 01:34:16
 */
public interface QuestionService extends IService<Question> {

    /**
     * 添加、更新题目时，检验题目是否合法
     *
     * @param question
     * @param add      是添加还是更新
     */
    void validQuestion(Question question, Boolean add);

    /**
     * 获取条件构造器
     *
     * @param questionQueryRequest
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取脱敏后的题目信息
     *
     * @param question
     * 没有传 request参数  ，后面需要再补上
     */
    QuestionVO getQuestionVO(Question question);

    /**
     * 分页获取脱敏后的题目信息列表
     *
     * @param questionPage
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage);

}
