package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.enums.TransTypeEnum;
import com.atguigu.srb.core.hfb.FormHelper;
import com.atguigu.srb.core.hfb.HfbConst;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.mapper.UserAccountMapper;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.bo.TransFlowBO;
import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.service.TransFlowService;
import com.atguigu.srb.core.service.UserAccountService;
import com.atguigu.srb.core.utils.LendNoUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2023-04-08
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private TransFlowService transFlowService;

    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);//检查常量是否正确
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);


        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
        return formStr;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> paramMap) {

        //幂等性判断
        //判断交易流水是否存在
        String agentBillNo = (String)paramMap.get("agentBillNo"); //商户充值订单号
        boolean isSave = transFlowService.isSaveTransFlow(agentBillNo);
        if(isSave){
            log.warn("幂等性返回");
            return "success";
        }


        //账户处理
        String bindCode = (String) paramMap.get("bindCode"); //充值人绑定协议号
        String chargeAmt = (String) paramMap.get("chargeAmt"); //充值金额
        baseMapper.updateAccount(bindCode, new BigDecimal(chargeAmt), new BigDecimal(0));


        //增加交易流水

        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,
                "充值");
        transFlowService.saveTransFlow(transFlowBO);

        return "success";


//
//        try {
//            Thread.sleep(6000);
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }

    }

    //查询账户余额
    @Override
    public BigDecimal getAccount(Long userId) {
        //根据userId查找用户账户
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", userId);
        UserAccount userAccount = baseMapper.selectOne(userAccountQueryWrapper);

        BigDecimal amount = userAccount.getAmount();
        return amount;
    }
}