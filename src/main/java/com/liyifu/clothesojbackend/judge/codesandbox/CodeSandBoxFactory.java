package com.liyifu.clothesojbackend.judge.codesandbox;

import com.liyifu.clothesojbackend.judge.codesandbox.impl.ExampleCodeSandBox;
import com.liyifu.clothesojbackend.judge.codesandbox.impl.RemoteCodeSandBox;
import com.liyifu.clothesojbackend.judge.codesandbox.impl.ThirdPartyCodeSandBox;

/**
 * 如果直接new了ExampleCodeSandBox()的实例，如果后期代码需要更换代码沙箱，那么就需要切换项目中所有代码沙箱相关的代码！
 * 于是使用 静态工厂模式 ，根据用户传入的字符串参数（沙箱类别），来生成对应的代码沙箱实现类
 * todo 静态工厂模式和动态工厂模式的区别
 *
 *  参数配置化，把项目中的一些可以交给用户去自定义的选项或字符串，写到配置文件中。
 *  这样开发者只需要改配置文件，而不需要去看你的项目代码，就能够自定义使用你项目的更多功能。
 */
public class CodeSandBoxFactory {
    /**
     * 创建代码沙箱实例
     * @param type 沙箱类别
     */
    public static CodeSandBox newInstance(String type){
        switch (type){
            case "example":
                return new ExampleCodeSandBox();
            case "remote":
                return new RemoteCodeSandBox();
            case "thirdParty":
                return new ThirdPartyCodeSandBox();
            default:
                return new ExampleCodeSandBox();
        }
    }
}
