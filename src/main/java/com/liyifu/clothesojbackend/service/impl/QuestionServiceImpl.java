package com.liyifu.clothesojbackend.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyifu.clothesojbackend.common.ErrorCode;
import com.liyifu.clothesojbackend.common.ResultUtils;
import com.liyifu.clothesojbackend.exception.BusinessException;
import com.liyifu.clothesojbackend.mapper.QuestionMapper;
import com.liyifu.clothesojbackend.model.dto.question.QuestionQueryRequest;
import com.liyifu.clothesojbackend.model.entity.Question;
import com.liyifu.clothesojbackend.model.entity.User;
import com.liyifu.clothesojbackend.model.vo.QuestionVO;
import com.liyifu.clothesojbackend.model.vo.UserVO;
import com.liyifu.clothesojbackend.service.QuestionService;
import com.liyifu.clothesojbackend.service.UserService;
import com.liyifu.clothesojbackend.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author liyifu
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2023-11-14 01:34:16
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    @Resource
    private UserService userService;

    /**
     * 添加、更新题目时，检验题目是否合法
     *
     * @param question
     * @param add      是添加还是更新
     */
    @Override
    public void validQuestion(Question question, Boolean add) {
        if (question == null) {
            throw new BusinessException((ErrorCode.PARAMS_ERROR));
        }
        //获取题目中的参数
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        //如果是添加题目，即add为true，那么创建题目所需的参数不能为空
        if (add) {
            if (StringUtils.isAnyBlank(title, content, tags)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        //对参数进行校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }

    }

    /**
     * 获取条件构造器
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper=new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        //获取分页请求信息
        Long id = questionQueryRequest.getId();
        Long userId = questionQueryRequest.getUserId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        //条件构造器
        queryWrapper.eq(id != null, "id", id)
                .eq(userId != null, "userId", userId)
                .eq("isDelete", false)
                .orderBy(SqlUtils.validSortField(sortField), "ascend".equals(sortOrder), sortField)
                .like(StringUtils.isNotBlank(title), "title", title)
                .like(StringUtils.isNotBlank(content), "content", content)
                .like(StringUtils.isNotBlank(answer), "answer", answer);
        //最后再查询tags条件
        if (CollectionUtil.isNotEmpty(tags)) {
            //遍历tags，对每一条tag进行条件筛选
            for (String tag : tags) {
                //todo   这里的like模糊查询看一下！
                queryWrapper.like("tags", tag);
            }
        }
        return queryWrapper;
    }

    /**
     * 获取脱敏后的题目信息
     *
     * @param question
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //首先需要将question转成questionVO，因为属性的类型不同，例如question的tags是字符串，questionVO的tags是list
        QuestionVO questionVO = QuestionVO.objToVO(question);
        //questionVO中还需要获取题目创建人的信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    /**
     * 分页获取脱敏后的题目信息列表
     *
     * @param questionPage
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage) {
        //获取question信息
        List<Question> questionList = questionPage.getRecords();
        //创建Page<QuestionVO>对象
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        //如果Page<Question>为空，则直接返回
        if (CollectionUtil.isEmpty(questionList)) {
            return questionVOPage;
        }

        //  获取每个题目的user信息  ,因为questionVO需要userVO   (这里的思想是 先把相关联的User全部查出来放入Map，后面再匹配对应的question！！！！)
        //      获取所有题目包含的userId，去除重复的
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        //      将查到的question创建的用户id作为key，用户user信息作为value放入map
        //      （这里的value为List<User>没有别的意思，存放的只是与userId匹配user信息，只是这里的格式需要List<User>）
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));

//        for (Question question : questionList) {   这里不用for循环，用stream流，因为stream流更优雅，性能更好！！！！
//            QuestionVO.objToVO(question);
//        }
        //对questionList的信息利用ObjToVO转换对应属性的类型 最后增加userVO信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            //转vo  (该方法内已经进行赋值操作！)
            QuestionVO questionVO = QuestionVO.objToVO(question);
            //增加userVO信息
            Long userId = question.getUserId();
            User user = null;
            //  如果该题的userId在map中出现过，那么为其添加userVO信息
            if (userIdUserListMap.containsKey(userId)) {
//                user = userService.getById(userId);  这里不需要重新查数据库，浪费性能，因为User类信息已经存放在map了
                user = userIdUserListMap.get(userId).get(0);
            }
            UserVO userVO = userService.getUserVO(user);
            questionVO.setUserVO(userVO);
            return questionVO;
        }).collect(Collectors.toList());

        //将questionVOList放入Page对象中
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }
}




