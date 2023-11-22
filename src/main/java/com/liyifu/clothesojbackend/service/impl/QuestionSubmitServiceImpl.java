package com.liyifu.clothesojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyifu.clothesojbackend.common.ErrorCode;
import com.liyifu.clothesojbackend.exception.BusinessException;
import com.liyifu.clothesojbackend.mapper.QuestionSubmitMapper;
import com.liyifu.clothesojbackend.model.dto.question.QuestionQueryRequest;
import com.liyifu.clothesojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.liyifu.clothesojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.liyifu.clothesojbackend.model.entity.Question;
import com.liyifu.clothesojbackend.model.entity.QuestionSubmit;
import com.liyifu.clothesojbackend.model.entity.User;
import com.liyifu.clothesojbackend.model.enums.QuestionSubmitLanguageEnum;
import com.liyifu.clothesojbackend.model.enums.QuestionSubmitStatusEnum;
import com.liyifu.clothesojbackend.model.vo.QuestionSubmitVO;
import com.liyifu.clothesojbackend.service.QuestionService;
import com.liyifu.clothesojbackend.service.QuestionSubmitService;
import com.liyifu.clothesojbackend.service.UserService;
import com.liyifu.clothesojbackend.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liyifu
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-11-14 01:34:16
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Long doSubmitQuestion(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //1、检验编程语言是否在题目给出的枚举值中
        String submitLanguage = questionSubmitAddRequest.getSubmitLanguage();
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(submitLanguage);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }

        //2、判断题目是否存在
        Long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //3、每个用户串行提交  todo(用限流更加合适 ，需要保证同一时间，用户只能提交成功一次)
        QuestionSubmit questionSubmit = new QuestionSubmit();
        Long userId = loginUser.getId();
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);
        questionSubmit.setSubmitLanguage(submitLanguage);
        questionSubmit.setSubmitCode(questionSubmitAddRequest.getSubmitCode());
        //设置初始判题状态
        questionSubmit.setSubmitState(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交失败");
        }
        //4、异步执行判题服务  todo (搞懂异步的过程写法）
        Long questionSubmitId = questionSubmit.getId();


        return questionSubmitId;
    }

    /**
     * 构造条件构造器
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQuestionSubmitQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        //构造器
        Long userId = questionSubmitQueryRequest.getUserId();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String submitLanguage = questionSubmitQueryRequest.getSubmitLanguage();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        queryWrapper.eq(StringUtils.isNotBlank(submitLanguage), "submitLanguage", submitLanguage)
                .eq(ObjectUtils.isNotEmpty(userId),"userId", userId)
                .eq(ObjectUtils.isNotEmpty(questionId),"questionId", questionId)
                .eq("isDelete", false)
                .orderBy(SqlUtils.validSortField(sortField), sortOrder.equals("ascend"), sortField);

        return queryWrapper;
    }

    /**
     * 获取脱敏后的题目提交信息
     * 管理员能看到所有人完整的题目提交信息，自己能看到自己提交的题目信息，其他用户只能看到非答案、提交代码的公开信息
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVO(questionSubmit);
        Long userId = loginUser.getId();
        if (userId != questionSubmitVO.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setSubmitCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * 分页获取脱敏后的题目提交信息
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        //获取Page<QuestionSubmit>中的数据list
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        long current = questionSubmitPage.getCurrent();
        long pageSize = questionSubmitPage.getSize();
        long total = questionSubmitPage.getTotal();

        //构建Page<QuestionSubmitVO>
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(current, pageSize, total);

        //对questionSubmitList进行脱敏
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(
                        questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());

        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




