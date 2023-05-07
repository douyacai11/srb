package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.bo.TransFlowBO;
import com.atguigu.srb.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author Helen
 * @since 2023-04-08
 */
public interface TransFlowService extends IService<TransFlow> {
    //保存交易流水
    void saveTransFlow(TransFlowBO transFlowBO);

    //判断流水是否存在
    boolean isSaveTransFlow(String agentBillNo);
}
