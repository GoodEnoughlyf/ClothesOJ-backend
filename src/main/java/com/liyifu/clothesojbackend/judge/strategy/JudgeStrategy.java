package com.liyifu.clothesojbackend.judge.strategy;

import com.liyifu.clothesojbackend.judge.codesandbox.model.JudgeInfo;

/**
 * 判题策略抽象接口
 *
 * 因为不同的语言有不同的判题策略，比如代码沙箱执行java语言时，比c语言要额外的花10秒，如果用if，else的话，那么语言越多就越复杂越需要修改，随着业务的复杂破坏开闭原则
 *      于是用策略模式进行优化
 */
public interface JudgeStrategy {
    /**
     * 执行判题策略方法
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
