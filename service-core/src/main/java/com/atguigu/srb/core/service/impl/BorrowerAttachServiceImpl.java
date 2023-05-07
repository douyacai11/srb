package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.pojo.entity.BorrowerAttach;
import com.atguigu.srb.core.mapper.BorrowerAttachMapper;
import com.atguigu.srb.core.pojo.vo.BorrowerAttachVO;
import com.atguigu.srb.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2023-04-08
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Override
    public List<BorrowerAttachVO> selectBorrowerAttachVOList(Long borrowerId) {

        QueryWrapper<BorrowerAttach> wrapper = new QueryWrapper<>();
        wrapper.eq("borrower_id",borrowerId);

        //把List<BorrowerAttach> 转为List<BorrowerAttachVO>
        List<BorrowerAttach> borrowerAttachList = baseMapper.selectList(wrapper);

        List<BorrowerAttachVO> borrowerAttachVOList=new ArrayList<>();
        borrowerAttachList.forEach(borrowerAttach -> {
            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();

            borrowerAttachVO.setImageType(borrowerAttach.getImageType());
            borrowerAttachVO.setImageUrl(borrowerAttach.getImageUrl());
            borrowerAttachVOList.add(borrowerAttachVO);
        });

        return borrowerAttachVOList;
    }
}
