package com.atguigu.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.atguigu.common.result.R;
import com.atguigu.srb.base.utils.JwtUtils;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.pojo.vo.UserBindVO;
import com.atguigu.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2023-04-08
 */
@Api(tags = "会员账号绑定")
@RestController
@RequestMapping("/api/core/userBind")
@Slf4j
public class UserBindController {

    @Resource
    private UserBindService userBindService;

    @ApiOperation("账号绑定提交数据")
    @PostMapping("/auth/bind")
    public R bind(@RequestBody UserBindVO userBindVO, HttpServletRequest request){

        //先校验是否登录:根据token中的userId来判断
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        //再绑定
        String formStr=  userBindService.commitBindUser(userBindVO,userId);
        return R.ok().data("formStr",formStr);

    }


    @ApiOperation("账户绑定异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){

        /**
         * 汇付宝发送请求到尚融宝
         * 1.把汇付宝传来的参数取出来
         * 21.验证参数中的sign     即 验签
         * 2.2验证成功 开始账户绑定  即同步功能
         *     账户绑定的主要逻辑   就是需要将尚融宝的两张表的信息和汇付宝那边的信息保持一致 即更新尚融宝user_info表和user_bind表
         *
         * */

        //switchMap:把汇付宝传来的参数类型为 Map<String, String[] 转换为 Map<String, Object> 类型
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());

        // toJSONString :是将对象转化为Json字符串
        log.info("账户绑定异步回调接受的参数如下"+ JSON.toJSONString(paramMap));


        if (!RequestHelper.isSignEquals(paramMap)){
            log.info("用户绑定异步回调签名验证错误"+JSON.toJSONString(paramMap));
        }

        //验签成功 开始账户绑定  即同步功能
        userBindService.notify(paramMap);

        return "success";

    }



}

