package com.liyifu.clothesojbackend.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.liyifu.clothesojbackend.judge.codesandbox.model.JudgeInfo;
import com.liyifu.clothesojbackend.model.dto.question.JudgeCase;
import com.liyifu.clothesojbackend.model.dto.question.JudgeConfig;
import com.liyifu.clothesojbackend.model.entity.Question;
import com.liyifu.clothesojbackend.model.enums.JudgeInfoMessageEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class JavaJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        Question question = judgeContext.getQuestion();
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        //默认返回成功信息
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
//        Long memory = judgeInfo.getMemory();
//        Long time = judgeInfo.getTime();
        //todo 选择编程
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L) ;
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L) ;
        //用于返回的judgeInfo信息
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);


        //判断输入沙箱的输入用例和输出用例数量是否相同
        if (inputList.size() != outputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        //比较每一项的输出结果是否与输入用例的预期结果相同
        for (int i = 0; i < outputList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            //  (貌似是man.java中的sout会多一个空行)
            String a = judgeCase.getOutput();
            String b = outputList.get(i);
            if (!a.equals(b)) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }

        //获取题目要求的配置 并且判断是否符合要求
        String jsonJudgeConfig = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(jsonJudgeConfig, JudgeConfig.class);
        Long timeLimit = judgeConfig.getTimeLimit();
        Long memoryLimit = judgeConfig.getMemoryLimit();
        if (memory > memoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        //java 程序额外执行10秒
        long JAVA_PROGRAM_TIME_COST = 10000L;
        if ((time - JAVA_PROGRAM_TIME_COST) > timeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
