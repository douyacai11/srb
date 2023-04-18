package com.atguigu.srb.sms.service;

import java.util.Map;

public interface SmsService {

    //发送验证码
    void send(String mobile, String templateCode, Map<String,Object> param);
}
