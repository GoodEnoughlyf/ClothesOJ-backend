package com.liyifu.clothesojbackend.judge.codesandbox.impl;

import com.liyifu.clothesojbackend.judge.codesandbox.CodeSandBox;
import com.liyifu.clothesojbackend.judge.codesandbox.CodeSandBoxFactory;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.liyifu.clothesojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.liyifu.clothesojbackend.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExampleCodeSandBoxTest {

    //冒号后面跟的是默认值，如果没有codesandbox.type，就传默认值
    @Value("${codesandbox.type:example}")
    private String type;

    @Test
    void executeCode(){
        /**
         * 这里直接new了ExampleCodeSandBox()的实例，如果后期代码需要更换代码沙箱，那么就需要切换项目中所有代码沙箱相关的代码！
         *  使用 工厂模式 ，根据用户传入的字符串参数（沙箱类别），来生成对应的代码沙箱实现类
         */
        CodeSandBox codeSandBox= new ExampleCodeSandBox();
        String code="int main() {}";
        String language= QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage(language);
        executeCodeRequest.setInputList(inputList);

        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }

    /**
     * 根据工厂模式执行代码沙箱
     */
    @Test
    void executeCodeByFactory(){
        System.out.println(type);
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        String code="int main() {}";
        String language= QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage(language);
        executeCodeRequest.setInputList(inputList);

        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }
}