package com.liyifu.clothesojbackend.judge.codesandbox.impl;

import com.liyifu.clothesojbackend.judge.codesandbox.CodeSandBox;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 示例沙箱代码 （仅仅为了跑通业务流程）
 */
public class ExampleCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("示例代码沙箱！");
        return null;
    }
}
