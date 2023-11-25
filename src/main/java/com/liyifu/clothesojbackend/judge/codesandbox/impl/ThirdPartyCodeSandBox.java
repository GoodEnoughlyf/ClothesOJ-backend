package com.liyifu.clothesojbackend.judge.codesandbox.impl;

import com.liyifu.clothesojbackend.judge.codesandbox.CodeSandBox;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 */
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
