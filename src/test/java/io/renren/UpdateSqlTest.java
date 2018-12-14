package io.renren;

import io.renren.modules.crm.entity.MerchInfoEntity;
import io.renren.modules.crm.service.MerchInfoService;
import io.renren.modules.sys.entity.SysDeptEntity;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysDeptService;
import io.renren.modules.sys.service.SysUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateSqlTest {

    @Autowired
    SysUserService userService;

    @Autowired
    SysDeptService sysDeptService;

    @Autowired
    MerchInfoService merchInfoService;


    @Test
    public void test2(){
        //(7,6,'力力数据',0,0),(8,6,'代理商',1,0),(9,8,'二级代理',0,0),(10,6,'商户',0,0);

        ArrayList waIds = new ArrayList();

        waIds.add(4l);
        waIds.add(5l);
        waIds.add(7l);
        waIds.add(205l);

       List<SysUserEntity> list =  userService.findAll();

       for(SysUserEntity entity : list){
           if(entity.getUserId() == 1){ //表示admin 底下所有商铺都为直隶商铺
               update(0l,1,entity);
               continue;
           }

           if(!waIds.contains(entity.getUserId())){
               continue;
           }

           SysDeptEntity dept = new SysDeptEntity();
           dept.setParentId(1l);
           dept.setName(entity.getUsername());
           dept.setDeptType(2);
           dept.setAddress("");
           dept.setIndustry("");
           dept.setLegalName("");
           sysDeptService.save(dept);

           update(dept.getDeptId(),4,entity);


       }


    }

    public void update(Long parentId,int type,SysUserEntity entity){

        List  userIds= new ArrayList<>();
        userIds.add(entity.getUserId());

        List<MerchInfoEntity> merchs =  merchInfoService.queryByUserId(userIds);
        for (MerchInfoEntity merch : merchs) {
            SysDeptEntity dept = new SysDeptEntity();
            dept.setParentId(parentId);
            dept.setName(merch.getMerchName());
            dept.setDeptType(type);
            dept.setAddress(merch.getAddress());
            dept.setIndustry(merch.getIndustry());
            dept.setLegalName(merch.getLegalName());
            sysDeptService.save(dept);
            merch.setDeptId(dept.getDeptId());
            merchInfoService.update(merch);
        }
    }
}
