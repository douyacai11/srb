package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.exception.BusinessException;
import com.atguigu.common.result.R;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.core.pojo.entity.IntegralGrade;
import com.atguigu.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2023-04-08
 */

@Slf4j
@Api(tags = "积分等级管理")
//@CrossOrigin
@RestController
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController {

    @Resource
    private IntegralGradeService integralGradeService;

    @ApiOperation("积分等级列表查询")
    @GetMapping("/list")
    public R listAll(){

        log.info("this is log info");
        log.warn("this is log warn");
        log.error("this is log error");

        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list).message("获取列表成功");
    }

    @ApiOperation("根据id删除积分等级的数据")
    @DeleteMapping("/remove/{id}")
    public R removeById(@PathVariable Long id){
        boolean result = integralGradeService.removeById(id);
        if (result){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }

    @ApiOperation("新增积分等级")
    @PostMapping("/save")
    public R save(
            @ApiParam(value = "积分等级对象",required = true)
            @RequestBody IntegralGrade integralGrade){

        if (integralGrade.getBorrowAmount()==null){
            //通过自定义类型的异常类 抛出 借款额度不合法的枚举
            throw  new BusinessException(ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        }

//        //断言解决
//        Assert.notNull(integralGrade.getBorrowAmount(),ResponseEnum.BORROW_AMOUNT_NULL_ERROR);

        boolean result = integralGradeService.save(integralGrade);

        if (result){
            return R.ok().message("新增成功");
        }else {
            return R.ok().message("保存失败");
        }

    }


    @ApiOperation("根据id获取积分等级")
    @GetMapping("/get/{id}")
    public R getById(@PathVariable Long id){
        IntegralGrade integralGrade = integralGradeService.getById(id);

        if (integralGrade!=null){
            return R.ok().data("record",integralGrade);
        }else {
            return R.error().message("数据获取失败");
        }
    }


    @ApiOperation("根据id更新积分等级")
    @PutMapping("/update")
    public R updateById(
            @ApiParam(value = "积分等级对象",required = true)
            @RequestBody IntegralGrade integralGrade){

        boolean result = integralGradeService.updateById(integralGrade);

        if (result){
            return R.ok().message("更新成功");
        }else {
            return R.ok().message("更新失败");
        }

    }

}

