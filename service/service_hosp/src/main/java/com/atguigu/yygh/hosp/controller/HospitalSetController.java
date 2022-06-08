package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags="医院设置")
@RestController
@RequestMapping("admin/hosp/hospitalSet")
public class HospitalSetController {

    //注入service进行调用
    @Autowired
    private HospitalSetService hospitalSetService;

    //查询医院所有信息
    //访问路径     http://localhost:8201/admin/hosp/hospitalSet/findall
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findall")
    public Result findAll(){
        //调用service方法
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);

    }

    //逻辑删除医院设置
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result remove(@PathVariable Long id){
        boolean flag = hospitalSetService.removeById(id);
        if(flag) {
            return Result.ok();
        }
        return Result.fail();
    }

    //带分页的条件查询
    @ApiOperation(value = "带分页条件查询医院设置")
    @PostMapping("findpageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        //创建分页查询所需page对象
        Page<HospitalSet> page = new Page<>(current,limit);
        //构建查询条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hoscode = hospitalSetQueryVo.getHoscode();
        String hosname = hospitalSetQueryVo.getHosname();
        if(!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname",hosname);
        }
        if(!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode",hoscode);
        }

        //调用方法实现分页查询
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper);
        //返回结果
        return Result.ok(pageHospitalSet);
    }


    //添加医院设置
    @ApiOperation(value="添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHostpitalSet(@RequestBody(required = true)HospitalSet hospitalSet){   //ReauestBody一般和@PostMapper一起用
        //设置状态 1：可用  0：不可用
        hospitalSet.setStatus(1);
        //设置密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }
        else{
            return Result.fail();
        }
    }

    //根据id获取医院设置
    @ApiOperation(value="根据id获取医院设置")
    @GetMapping("getHospitalSet/{id}")
    public Result getHospitalSet(@PathVariable long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    //修改医院设置
    //先做查询，查询之后回显，然后修改
    @ApiOperation(value="修改医院设置")
    @PostMapping("updateHsopitalSet")
    public Result updateHsopitalSet(@RequestBody(required = true) HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if(flag){
            return Result.ok();
        }
        else{
            return Result.fail();
        }
    }

    //批量删除医院设置(逻辑删除)
    @ApiOperation(value="批量删除医院设置")
    @DeleteMapping("batchRemove")
    public Result batchremoveHospitalSet(@RequestBody List<Long> idlist){
        hospitalSetService.removeByIds(idlist);
        return Result.ok();
    }

    //医院设置的锁定和解锁状态（表中的status字段的值）
    @ApiOperation(value="医院设置的锁定和解锁状态设置")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    //发送签名密钥
    @ApiOperation(value="发送签名密钥")
    @PutMapping("sendKey/{id}")
    public Result sendKeyHostpitalSet(@PathVariable Long id){
        HospitalSet hospitalset = hospitalSetService.getById(id);
        String signKey = hospitalset.getSignKey();
        String hoscode = hospitalset.getHoscode();
        //TODO  发送短信
        return Result.ok();
    }
}
