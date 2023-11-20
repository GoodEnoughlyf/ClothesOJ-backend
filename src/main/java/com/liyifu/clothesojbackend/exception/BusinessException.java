package com.liyifu.clothesojbackend.exception;

import com.liyifu.clothesojbackend.common.ErrorCode;

/**
 * 自定义异常封装类
 */
public class BusinessException extends RuntimeException{

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code,String message){
//        子类继承父类 ，如果没有无参构造，那么必须supper（）   这里继承的RuntimeException的构造函数需要参数（String message），所以这里传message
        super(message);
        this.code=code;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code=errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode,String message){
        super(message);
        this.code=errorCode.getCode();
    }

    public int getCode(){
        return code;
    }
}
