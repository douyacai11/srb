package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.enums.BorrowerStatusEnum;
import com.atguigu.srb.core.mapper.BorrowerAttachMapper;
import com.atguigu.srb.core.mapper.BorrowerMapper;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.entity.BorrowerAttach;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.pojo.vo.BorrowerVO;
import com.atguigu.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2023-04-08
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);

        //保存借款人信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());//认证中

        baseMapper.insert(borrower);


        //保存附件
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });

        //更新user_info中的借款人认证状态
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);


    }

    //获取借款人认证状态
    @Override
    public Integer getStatusByUserId(Long userId) {

        QueryWrapper<Borrower> wrapper = new QueryWrapper<>();

        //根据user_id 查询Borrower表中的status字段的值
        wrapper.select("status").eq("user_id",userId);
        List<Object> objects = baseMapper.selectObjs(wrapper);

        if (objects.size() ==0){
            //借款人尚未提交信息
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        Integer status = (Integer) objects.get(0);
        return status;
    }

    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {
        //没有查询条件就正常返回 有就QueryWrapper
        if (keyword==null){
            baseMapper.selectPage(pageParam,null);
        }
        QueryWrapper<Borrower> wrapper = new QueryWrapper<>();
        wrapper.like("name", keyword)
                .or().like("id_card", keyword)
                .or().like("mobile", keyword)
                .orderByDesc("id");
        return baseMapper.selectPage(pageParam,wrapper);
    }
}
