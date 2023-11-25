package com.liyifu.clothesojbackend.judge.codesandbox;

import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandBox {
    /**
     * 代码沙箱执行代码接口
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
