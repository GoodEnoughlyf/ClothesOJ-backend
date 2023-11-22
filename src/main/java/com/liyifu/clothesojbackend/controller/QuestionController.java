package com.liyifu.clothesojbackend.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyifu.clothesojbackend.annotation.AuthCheck;
import com.liyifu.clothesojbackend.common.BaseResponse;
import com.liyifu.clothesojbackend.common.ErrorCode;
import com.liyifu.clothesojbackend.common.ResultUtils;
import com.liyifu.clothesojbackend.constant.UserConstant;
import com.liyifu.clothesojbackend.exception.BusinessException;
import com.liyifu.clothesojbackend.model.dto.question.*;
import com.liyifu.clothesojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.liyifu.clothesojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.liyifu.clothesojbackend.model.entity.Question;
import com.liyifu.clothesojbackend.model.entity.QuestionSubmit;
import com.liyifu.clothesojbackend.model.entity.User;
import com.liyifu.clothesojbackend.model.vo.QuestionSubmitVO;
import com.liyifu.clothesojbackend.model.vo.QuestionVO;
import com.liyifu.clothesojbackend.service.QuestionService;
import com.liyifu.clothesojbackend.service.QuestionSubmitService;
import com.liyifu.clothesojbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    /**
     * 创建题目
     *
     * @param questionAddRequest
     * @param request            question需要获取创建者userId
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //   1、创建题目
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        //前端questionAddRequest获取的tags、judgeConfig和judgeCase和数据库存储类型不同，需要转换
        //      questionAddRequest的tags是List，但是数据库需要的是json数组字符串
        List<String> tagList = questionAddRequest.getTags();
        if (tagList != null) {
            String tags = JSONUtil.toJsonStr(tagList);
            question.setTags(tags);
        }

        //      questionAddRequest的judgeConfig是judgeConfig对象，但是数据库需要的是json对象字符串
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            String jsonJudgeConfig = JSONUtil.toJsonStr(judgeConfig);
            question.setJudgeConfig(jsonJudgeConfig);
        }

        //      questionAddRequest的judgeCase是judgeCase对象list，但是数据库需要的是json对象字符串
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if (judgeCase != null) {
            String jsonJudgeCase = JSONUtil.toJsonStr(judgeCase);
            question.setJudgeCase(jsonJudgeCase);
        }

        //校验题目是否符合要求
        questionService.validQuestion(question, true);

        //  2、给题目userId
        //获取当前登录用户，给创建的question赋值userId
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        question.setUserId(userId);

        boolean result = questionService.save(question);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        return ResultUtils.success(question.getId());
    }


    /**
     * 删除题目  (仅本人创建的题目能删除 ，或者管理员能删除)
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody QuestionDeleteRequest questionDeleteRequest, HttpServletRequest request) {
        if (questionDeleteRequest == null || questionDeleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取当前用户
        User loginUser = userService.getLoginUser(request);
        //判断需要删除的题目是否存在
        Question oldQuestion = questionService.getById(questionDeleteRequest.getId());
        if (oldQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //仅用户能删除自己创建题目  或者管理员删除
        if (!loginUser.getId().equals(oldQuestion.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.removeById(questionDeleteRequest.getId());
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(result);
    }

    /**
     * 更新题目  （仅限管理员）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        //前端传的tags、judgeConfig和judgeCase和数据库的类型不同，需要转化
        List<String> tagList = questionUpdateRequest.getTags();
        if (tagList != null) {
            String tags = JSONUtil.toJsonStr(tagList);
            question.setTags(tags);
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            String jsonJudgeConfig = JSONUtil.toJsonStr(judgeConfig);
            question.setJudgeConfig(jsonJudgeConfig);
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            String jsonJudgeCase = JSONUtil.toJsonStr(judgeCase);
            question.setJudgeCase(jsonJudgeCase);
        }
        //题目校验
        questionService.validQuestion(question, false);
        //判断题目是否存在
        Question oldQuestion = questionService.getById(questionUpdateRequest.getId());
        if (oldQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean result = questionService.updateById(question);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(result);

    }

    /**
     * 根据id获取题目信息  （用户获取自己的题目信息不需要脱敏、管理员获取也不需要脱敏）
     */
    @PostMapping("/get")
    public BaseResponse<Question> getQuestionById(Long id,HttpServletRequest request){
        if(id==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if(question==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //不是本人创建的题目 或者 管理员 ，不能获取题目信息
        User loginUser = userService.getLoginUser(request);
        if(!loginUser.getId().equals(question.getUserId()) && !userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     * 根据id获取脱敏后题目信息
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(Long id){
        if(id==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if(question==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        QuestionVO questionVO = questionService.getQuestionVO(question);
        return ResultUtils.success(questionVO);
    }

    /**
     * 分页后去脱敏后的题目列表信息
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest){
        long current = questionQueryRequest.getCurrent();
        long pageSize = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, pageSize), questionService.getQueryWrapper(questionQueryRequest));
        Page<QuestionVO> questionVOPage = questionService.getQuestionVOPage(questionPage);
        return ResultUtils.success(questionVOPage);
    }

    /**
     * 分页获取当前用户创建的 脱敏后的题目信息
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,HttpServletRequest request){
        if(questionQueryRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionQueryRequest.getCurrent();
        long pageSize = questionQueryRequest.getPageSize();
        //获取当前用户
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());

        Page<Question> questionPage = questionService.page(new Page<>(current, pageSize), questionService.getQueryWrapper(questionQueryRequest));
        Page<QuestionVO> questionVOPage = questionService.getQuestionVOPage(questionPage);
        return ResultUtils.success(questionVOPage);
    }

    /**
     * 分页获取题目列表  （仅限管理员）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,HttpServletRequest request){
        if(questionQueryRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionQueryRequest.getCurrent();
        long pageSize = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, pageSize), questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 用户 编辑题目
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest,HttpServletRequest request){
        if(questionEditRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest,question);
        //QuestionEditRequest的tags、judgeConfig和judgeCase类型和数据库不一致
        List<String> tagList = questionEditRequest.getTags();
        if(tagList!=null){
            String tags = JSONUtil.toJsonStr(tagList);
            question.setTags(tags);

        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if(judgeConfig!=null){
            String jsonJudgeConfig = JSONUtil.toJsonStr(judgeConfig);
            question.setJudgeConfig(jsonJudgeConfig);
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if(judgeCase!=null){
            String jsonJudgeCase = JSONUtil.toJsonStr(judgeCase);
            question.setJudgeCase(jsonJudgeCase);
        }

        //校验题目是否符合规范
        questionService.validQuestion(question,false);

        //仅用户本人 或者管理员可以编辑题目
        //  获取当前用户
        User loginUser = userService.getLoginUser(request);

        //判断题目是否存在
        Question oldQuestion = questionService.getById(questionEditRequest.getId());
        if(oldQuestion==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if(!loginUser.getId().equals(oldQuestion.getUserId()) && !userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        if(!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        return ResultUtils.success(result);
    }

    /**
     * 提交题目
     */
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request){
        if(questionSubmitAddRequest==null || questionSubmitAddRequest.getQuestionId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //登录才能提交
        User loginUser = userService.getLoginUser(request);
        Long questionSubmitId = questionSubmitService.doSubmitQuestion(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);

    }

    /**
     * 分页获取题目提交列表（除了管理员和自己外，其他用户只能看到非答案、提交代码的公开信息）
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,HttpServletRequest request){
        long current = questionSubmitQueryRequest.getCurrent();
        long pageSize = questionSubmitQueryRequest.getPageSize();

        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, pageSize), questionSubmitService.getQuestionSubmitQueryWrapper(questionSubmitQueryRequest));

        //获取登录用户
        User loginUser = userService.getLoginUser(request);

        //对用户数据进行脱敏
        Page<QuestionSubmitVO> questionSubmitVOPage = questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser);
        return ResultUtils.success(questionSubmitVOPage);
    }
}
