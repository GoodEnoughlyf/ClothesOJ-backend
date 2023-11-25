package com.liyifu.clothesojbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.liyifu.clothesojbackend.judge.codesandbox.model.JudgeInfo;
import com.liyifu.clothesojbackend.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data
public class QuestionSubmitVO implements Serializable {
    private Long id;

    private Long questionId;

    private Long userId;

    private JudgeInfo judgeInfo;

    private String submitLanguage;

    private String submitCode;

    private Integer submitState;

    private Date createTime;

    private Date updateTime;

    private UserVO userVO;

    private QuestionVO questionVO;

    //因为数据库存储的判题信息judgeInfo是json格式的字符串，这里将其封装为了judgeInfo类，需要对其进行转化
    public static QuestionSubmitVO objToVO(QuestionSubmit questionSubmit){
        if(questionSubmit==null){
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit,questionSubmitVO);

        //除了judgeInfo在数据库与vo不同，还有vo多了UserVO和questionVO
        String jsonJudgeInfo = questionSubmit.getJudgeInfo();
        JudgeInfo judgeInfo = JSONUtil.toBean(jsonJudgeInfo, JudgeInfo.class);
        questionSubmitVO.setJudgeInfo(judgeInfo);

        return questionSubmitVO;
    }

    public static QuestionSubmit VOToObj(QuestionSubmitVO questionSubmitVO){
        if(questionSubmitVO==null){
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO,questionSubmit);

        JudgeInfo judgeInfo = questionSubmitVO.getJudgeInfo();
        if(judgeInfo!=null){
            String jsonJudgeInfo = JSONUtil.toJsonStr(judgeInfo);
            questionSubmit.setJudgeInfo(jsonJudgeInfo);
        }

        return questionSubmit;
    }
}
