package com.atguigu.srb.core.controller.admin;

import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.vo.BorrowerApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
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




    @ApiOperation("根据id获取借款人信息")
    @GetMapping("/show/{id}")
    public  R show(@PathVariable long id){

        //获取封装对象:BorrowerDetailVO 返回前端
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);
        return  R.ok().data("borrowerDetailVO",borrowerDetailVO);
    }


    //额度审批的目标：
    //(1)在user_integral表中添加积分明细
    //(2)在user_info表中添加总积分（user_info表中的原始积分 + user_integral表中的积分明细之和 ）
    //(3)修改borrower表的借款申请审核状态
    //(4)修改user_info表中的借款申请审核状态
    @ApiOperation("借款额度审批")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO){
        borrowerService.approval(borrowerApprovalVO);
        return R.ok().message("审批完成");

    }
}
