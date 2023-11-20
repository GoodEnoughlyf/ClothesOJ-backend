package com.liyifu.clothesojbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.liyifu.clothesojbackend.model.dto.question.JudgeConfig;
import com.liyifu.clothesojbackend.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class QuestionVO implements Serializable {
    private Long id;

    /**
     * 创建该题目的用户id
     */
    private Long userId;

    private String title;

    private String content;

    /**
     * 标签列表  数据库用json数组存储，便于前端获取展示
     * 所以返回给前端一个list最方便获取值
     */
    private List<String> tags;

    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建题目人的封装类
     */
    private UserVO userVO;

    /**
     * 将数据库的json字符串格式的数据转化为 可供前端直接获取的json对象
     */
    public static QuestionVO objToVO(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        //由于question和questionVO的tags和judgeConfig类型不同，需要转换
        //      tags在question中是json数组字符串，将其转成list
        String jsonTags = question.getTags();
        List<String> tagList = JSONUtil.toList(jsonTags, String.class);
        questionVO.setTags(tagList);
        //      judgeConfig在question中是json对象字符串，将其转成judgeConfig对象
        String jsonJudgeConfig = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(jsonJudgeConfig, JudgeConfig.class);
        questionVO.setJudgeConfig(judgeConfig);
        return questionVO;
    }


    /**
     * 将获取的前端json对象转化为 可直接存储在数据库的json字符串格式的数据
     */
    public static Question VOToObj(QuestionVO questionVO) {
        if(questionVO==null){
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO,question);
        //由于question和questionVO的tags和judgeConfig类型不同，需要转换
        //      tags在questionVO中是list，将其转成json数组字符串
        List<String> tagList = questionVO.getTags();
        if(tagList!=null){
            String jsonStr = JSONUtil.toJsonStr(tagList);
            question.setTags(jsonStr);
        }
        //      judgeConfig在question中是judgeConfig对象，将其转成json对象字符串
        JudgeConfig judgeConfig = questionVO.getJudgeConfig();
        if(judgeConfig!=null){
            String jsonJudgeConfig = JSONUtil.toJsonStr(judgeConfig);
            question.setJudgeConfig(jsonJudgeConfig);
        }
        return question;
    }
}
