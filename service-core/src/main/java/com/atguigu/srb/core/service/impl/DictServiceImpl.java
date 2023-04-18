package com.atguigu.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.srb.core.listener.ExcelDictDTOListener;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import com.atguigu.srb.core.pojo.entity.Dict;
import com.atguigu.srb.core.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2023-04-08
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    private RedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream inputStream) {  //读取excel文件
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("Excel导入成功");
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = baseMapper.selectList(null);

        //把将Dict列表转换成ExcelDictDTO列表:先创建ExcelDictDTO列表

        ArrayList<ExcelDictDTO> excelDictDTOList = new ArrayList<>(dictList.size());
        dictList.forEach(dict -> {

            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict, excelDictDTO);
            excelDictDTOList.add(excelDictDTO);
        });
        return excelDictDTOList;
    }


    //根据id查询数据字典  (设置在redis中存储和查询
    @Override
    public List<Dict> listByParentId(Long parentId) {


        /**
         * 1.先查询redis中是否存在数据列表
         *
         * 2.1如果存在 则从redis中直接返回数据列表
         *
         * 2.2不存在 就查询数据库 将数据存入redis 再返回数据列表
         * */

        try {
            log.info("从redis中取出数据");
            List<Dict> dictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);

            if (dictList !=null){
                return dictList;
            }

        }

        catch (Exception e) {
            log.error("redis服务器异常"+ ExceptionUtils.getStackTrace(e));
        }


        log.info("从数据库中获取数据列表");
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",parentId);
        List<Dict> dictList = baseMapper.selectList(wrapper);

        //填充hasChildren字段
        dictList.forEach(dict -> {
            //判断当前节点是否有子节点,找到当前dict的下级有没有子节点
            boolean hasChildren = this.hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        });

        try {
            //将数据存入redis中
            log.info("将数据存入redis中");
            redisTemplate.opsForValue().set("srb:core:dictList:"+parentId,dictList,5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis服务器异常"+ ExceptionUtils.getStackTrace(e));
        }

        return  dictList;
    }

    /**
     * 判断当前id下面是否有子节点
     * */
    private boolean hasChildren(Long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count.intValue() > 0){
           return true;
        }
        return false;
    }
}