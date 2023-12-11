package com.liyifu.clothesojbackend.judge;

import cn.hutool.json.JSONUtil;
import com.liyifu.clothesojbackend.common.ErrorCode;
import com.liyifu.clothesojbackend.exception.BusinessException;
import com.liyifu.clothesojbackend.judge.codesandbox.CodeSandBox;
import com.liyifu.clothesojbackend.judge.codesandbox.CodeSandBoxFactory;
import com.liyifu.clothesojbackend.judge.codesandbox.CodeSandBoxProxy;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.liyifu.clothesojbackend.judge.codesandbox.model.JudgeInfo;
import com.liyifu.clothesojbackend.judge.strategy.JudgeContext;
import com.liyifu.clothesojbackend.model.dto.question.JudgeCase;
import com.liyifu.clothesojbackend.model.entity.Question;
import com.liyifu.clothesojbackend.model.entity.QuestionSubmit;
import com.liyifu.clothesojbackend.model.enums.QuestionSubmitLanguageEnum;
import com.liyifu.clothesojbackend.model.enums.QuestionSubmitStatusEnum;
import com.liyifu.clothesojbackend.service.QuestionService;
import com.liyifu.clothesojbackend.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题服务实现类
 */

@Service
public class JudgeServiceImpl implements JudgeService {

    //代码沙箱类型
    @Value("${codesandbox.type}")
    private String type;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Resource
    private JudgeManager judgeManager;

    /**
     * 判题
     *
     * @param questionSubmitId
     * @return
     */
    @Override
    public QuestionSubmit doJudge(Long questionSubmitId) {
        //1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在!");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目信息不存在！");
        }

        //2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getSubmitState().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "正在判题中");
        }
        //3）更改判题（题目提交）的状态为 “判题中”，防止重复执行，也能让用户即时看到状态
        QuestionSubmit updateQuestionSubmit = new QuestionSubmit();
        updateQuestionSubmit.setId(questionSubmitId);
        updateQuestionSubmit.setSubmitState(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(updateQuestionSubmit);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新失败");
        }

        //4）调用沙箱，获取到执行结果
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        CodeSandBoxProxy codeSandBoxProxy = new CodeSandBoxProxy(codeSandBox);
        String submitCode = questionSubmit.getSubmitCode();
        String submitLanguage = QuestionSubmitLanguageEnum.JAVA.getValue();
        String jsonJudgeCase = question.getJudgeCase();
        //数据库存储的判题用例是一个json数组字符串，将其转化为一个对象list
        List<JudgeCase> judgeCaseList = JSONUtil.toList(jsonJudgeCase, JudgeCase.class);
        //将List集合转成ExecuteCodeRequest代码沙箱请求类需要的List集合形式,只需要获取输入用例的list
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(inputList);
        executeCodeRequest.setCode(submitCode);
        executeCodeRequest.setLanguage(submitLanguage);

        //得到代码沙箱执行的返回结果list
        ExecuteCodeResponse executeCodeResponse = codeSandBoxProxy.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();

        //5）根据沙箱的执行结果，设置题目的判题状态和信息  (给JudgeStrategy策略模式的上下文context赋值，同时选择哪一种策略模式）
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
            //选择策略模式
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        //6）修改判题结果
        updateQuestionSubmit = new QuestionSubmit();
        updateQuestionSubmit.setId(questionSubmitId);
        updateQuestionSubmit.setSubmitState(QuestionSubmitStatusEnum.SUCCESSED.getValue());
        updateQuestionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(updateQuestionSubmit);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新失败！");
        }
            //再次查询数据库，返回最新的提交信息
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionId);
        return questionSubmitResult;
    }
}
