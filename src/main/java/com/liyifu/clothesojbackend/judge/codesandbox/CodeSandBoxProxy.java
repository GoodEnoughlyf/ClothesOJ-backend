package com.liyifu.clothesojbackend.judge.codesandbox;

import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用静态代理对代码沙箱进行功能增强，在调用代码沙箱前后打印日志
 *
 * 为什么使用静态代理？
 *      因为针对于代码沙箱模块，不需要扩充额外的功能，只需要打印日志，接口不用增加新的方法
 *      且对于代码沙箱模块，只有一个代码沙箱类，不需要对多个目标类创建代理类
 *      最重要的一点是：前面用了工厂模式，根据用户选择的type，已经知道new是哪一个代码沙箱对象，    不需要用动态代理，考虑传的是哪一个对象！！！！
 */
@Slf4j
public class CodeSandBoxProxy implements CodeSandBox{

    private final CodeSandBox codeSandBox;

    public CodeSandBoxProxy(CodeSandBox codeSandBox) {
        this.codeSandBox = codeSandBox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息："+executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息："+executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
