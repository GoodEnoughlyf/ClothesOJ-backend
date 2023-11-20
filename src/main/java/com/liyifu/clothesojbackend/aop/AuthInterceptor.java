package com.liyifu.clothesojbackend.aop;

import com.liyifu.clothesojbackend.annotation.AuthCheck;
import com.liyifu.clothesojbackend.common.ErrorCode;
import com.liyifu.clothesojbackend.constant.UserConstant;
import com.liyifu.clothesojbackend.exception.BusinessException;
import com.liyifu.clothesojbackend.model.entity.User;
import com.liyifu.clothesojbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验AOP
 * @Aspect 注解表示该类为一个切面
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 对所有带有@AuthCheck自定义注解的方法进行AOP切面编程，即将该方法作为切面插入
     * @param joinPoint 连接点
     * @param authCheck 约定的自定义注解
     *     只能在Controller中加入request参数，但该切面需要获取request的请求信息来获取当前用户的信息，所以用RequestContextHolder类来获取request！！！
     *       https://www.cnblogs.com/zwh0910/p/17168833.html   （详细介绍了RequestContextHolder类）
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        //1、获取连接点 有没有给自定义注解中的mustRole赋值
        String mustRole = authCheck.mustRole();
        //获取request
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        //2、获取当前登录用户的userRole
        User loginUser = userService.getLoginUser(request);
        //3、比较userRole和mustRole是否匹配
        //必须有mustRole才执行，也就是在需要切面的连接点加上自定义注解AuthCheck且赋值
        if(StringUtils.isNotBlank(mustRole)){
            //如果mustRole的值不是管理员,则拒绝
            if(mustRole.equals(UserConstant.DEFAULT_ROLE)){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            //如果mustRole的值为ban，直接拒绝
            if(mustRole.equals(UserConstant.BAN_ROLE)){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            //管理员才通过  接着比较mustRole与当前登录用户的userRole
            if(mustRole.equals(UserConstant.ADMIN_ROLE)){
                //如果mustRole与userRole不匹配
                if(!loginUser.getUserRole().equals(mustRole)){
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
        }

        //通过权限校验，放行
        return joinPoint.proceed();
    }
}
