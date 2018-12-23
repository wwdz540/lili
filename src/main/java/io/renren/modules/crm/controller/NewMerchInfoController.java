package io.renren.modules.crm.controller;


import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;

import io.renren.modules.crm.entity.NewMerchInfoEntity;
import io.renren.modules.crm.service.NewMerchInfoService;
import io.renren.modules.sys.entity.SysUserEntity;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商户管理
 *
 * @author zhuhui
 */
@RestController
@RequestMapping("/crm/newmerchInfo")
public class NewMerchInfoController {

    @Autowired
    NewMerchInfoService service ;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        SysUserEntity user = getUser();
        if(user.getDeptId()!=1) //如果不顶层商铺管理员 就要加上过滤条件
        {

            params.put("parentId",user.getDeptId());
            params.put("orDeptId",user.getDeptId());
        }

        //处理查询参数问题
        Query query = new Query(params);
        List<NewMerchInfoEntity> list = service.queryList(query);
        int total = service.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }


    @RequestMapping("/save")
    public R save(@RequestBody NewMerchInfoEntity entity){
        ValidatorUtils.validateEntity(entity);
        service.save(entity);
        return R.ok();
    }
    @RequestMapping("/update")
    public R update(@RequestBody NewMerchInfoEntity entity){
        ValidatorUtils.validateEntity(entity);
        service.update(entity);
        return R.ok();
    }
    @RequestMapping("/info/{merchId}")
    public R info(@PathVariable("merchId") int merchId){
        NewMerchInfoEntity data = service.findOne(merchId);
        return R.ok().put("data", data);
    }


    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] merchIds){
        service.deleteBatch(merchIds);
        return R.ok();
    }


    public SysUserEntity getUser(){
        SysUserEntity userEntity =  (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
        return userEntity;
    }


}
