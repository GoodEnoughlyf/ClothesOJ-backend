package com.liyifu.clothesojbackend.judge.codesandbox.impl;

import com.liyifu.clothesojbackend.judge.codesandbox.CodeSandBox;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeResponse;

public class RemoteCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        return null;
    }
}
