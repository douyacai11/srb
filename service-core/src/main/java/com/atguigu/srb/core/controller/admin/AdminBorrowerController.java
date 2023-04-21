package com.atguigu.srb.core.controller.admin;

import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "借款人管理")
@RestController
@RequestMapping("/admin/core/borrower")
@Slf4j
public class AdminBorrowerController {

    @Resource
    private BorrowerService borrowerService;


    @ApiOperation("获取借款人分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(@PathVariable Long page, @PathVariable Long limit, @RequestParam String keyword){

        //分页多条件查询 就是先new Page对象传入你需要的参数 返回值是IPage 且分页插件的主要方法是selectPage
        Page<Borrower> pageParam = new Page<>(page,limit);

        IPage<Borrower> pageModel = borrowerService.listPage(pageParam,keyword);
        return R.ok().data("pageModel",pageModel);
    }

}
