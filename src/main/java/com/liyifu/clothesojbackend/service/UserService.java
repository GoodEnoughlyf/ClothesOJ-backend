package com.liyifu.clothesojbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyifu.clothesojbackend.model.dto.user.UserQueryRequest;
import com.liyifu.clothesojbackend.model.entity.User;
import com.liyifu.clothesojbackend.model.vo.LoginUserVO;
import com.liyifu.clothesojbackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author liyifu
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-11-14 01:34:16
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return
     */
    long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param userPassword 用户米密码
     * @return 给前端返回已脱敏数据
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员 （1）
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员 （2）
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取单个脱敏的用户信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取登录用户的脱敏信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息集合
     */
    List<UserVO> getUserVO(List<User> users);

    /**
     * 对获取条件构造器进行封装
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
