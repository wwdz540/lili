package io.renren.modules.crm.controller;


import io.renren.common.exception.RRException;
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
public class NewMerchInfoController extends BaseController {

    @Autowired
    NewMerchInfoService service ;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        SysUserEntity user = getUser();
        if(user.getMcId()!=1) //如果不顶层商铺管理员 就要加上过滤条件
        {
            params.put("parentId",user.getMcId());
         //   params.put("orDeptId",user.getMcId());
        }

        if(params.get("parentId")!=null ){
            params.put("orDeptId",params.get("parentId"));
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
       //{0:"平台",1:"商户",2:"代理商",3:"集团公司",4:"代理子帐户",5:"集团公司子帐户"};
        NewMerchInfoEntity userMerch = getCurrentMerch();
        NewMerchInfoEntity parent =service.findOne(entity.getParentId());
        if(userMerch.getId() != 1){ //如果不是顶级商户的话
            // parent =service.findOne(entity.getParentId());
            if(!parent.getPath().startsWith(userMerch.getPath())){ //表明他不是要添加到其底下子商户中
                entity.setParentId(userMerch.getId());
                parent = userMerch;
            }

        }

       // System.out.println("deptType"+entity.getDeptType());
            switch (parent.getDeptType()){
                case 0:
                        if(!(entity.getDeptType()==1||
                                        entity.getDeptType()==2||
                                        entity.getDeptType()==3)){
                            System.err.println("参数不正确");
                            entity.setDeptType(1);
                        }
                        break;
                case 2:
                    if(!(entity.getDeptType()==3||
                            entity.getDeptType()==4)){
                        System.err.println("参数不正确");
                        entity.setDeptType(4); //设为代理子账户
                    }
                    break;
                case 3:
                    if(!(entity.getDeptType()==5)){
                        System.err.println("参数不正确");
                        entity.setDeptType(5);
                    }
                    break;
                 default:
                     throw new RRException("当前商户不可以添加子商户");
            }

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
