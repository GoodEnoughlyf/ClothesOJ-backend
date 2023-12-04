package com.liyifu.clothesojbackend.judge.strategy;

import com.liyifu.clothesojbackend.judge.codesandbox.model.JudgeInfo;
import com.liyifu.clothesojbackend.model.dto.question.JudgeCase;
import com.liyifu.clothesojbackend.model.entity.Question;
import com.liyifu.clothesojbackend.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上层的调用需要与接口之间有一定的交互。交互的可能是一些属性，或是一些方法。这样的交互往往会让接口变的难以调用；于是上下文的引入就是势在必行。
 * 将相关的属性或一些 公共 的方法封装到上下文中，让上下文去和接口进行复杂的交互。而上层的调用只需要跟上下文打交道就可以。
 * 策略模式仅仅封装算法，提供新的算法插入到已有系统中，策略模式并不决定在何时使用何种算法。在什么情况下使用什么算法是由客户端决定的。

 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
