package io.renren;

import io.renren.modules.crm.entity.MerchInfoEntity;
import io.renren.modules.crm.entity.NewMerchInfoEntity;
import io.renren.modules.crm.service.MerchInfoService;
import io.renren.modules.crm.service.NewMerchInfoService;
import io.renren.modules.sys.dao.SysUserDao;
import io.renren.modules.sys.entity.SysDeptEntity;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysDeptService;
import io.renren.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateSqlTest {

    @Autowired
    SysUserService userService;

    @Autowired
    SysDeptService sysDeptService;

    @Autowired
    MerchInfoService merchInfoService;

    @Autowired
    SysUserDao userDao;

    @Autowired
    NewMerchInfoService newMerchInfoService;

    /****/
    private WeakHashMap<Long,String> parenId2Path;

    @Test
    public void test2(){
        //(7,6,'力力数据',0,0),(8,6,'代理商',1,0),(9,8,'二级代理',0,0),(10,6,'商户',0,0);

        ArrayList waIds = new ArrayList();

        /**查询出来的代理*/
        waIds.add(4l);
        waIds.add(5l);
        waIds.add(7l);
        waIds.add(205l);

       List<SysUserEntity> list =  userService.findAll();
        //更新所有代理商，及代理商底下的用户
       for(SysUserEntity entity : list){
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
           entity.setDeptId(dept.getDeptId());
           userDao.update(entity);
           update(dept.getDeptId(),4,entity);

       }

       //更新所有直隶商户数据
        for (SysUserEntity entity : list) {
            if(waIds.contains(entity.getUserId())){
                continue;
            }
            if(entity.getUserId() == 1){ //表示admin 底下所有商户都为直隶商铺
                update(1l,1,entity);
                continue;
            }
        }

        updatePath();


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


    public String getParentPath(Long parentId){
        String parentPath = parenId2Path.get(parentId);
        if(parentPath == null){
            parentPath = sysDeptService.queryObject(parentId).getPath();
            parenId2Path.put(parentId,parentPath);
        }
        return parentPath;
    }

   // @Test
    public void updatePath(){



            Map p = new HashMap();

            List<NewMerchInfoEntity> list =newMerchInfoService.queryList(p);

            Collections.sort(list, new Comparator<NewMerchInfoEntity>() {
                @Override
                public int compare(NewMerchInfoEntity o1, NewMerchInfoEntity o2) {
                    return (int)(o1.getId()-o2.getId());
                }
            });
            //list.stream().map(d->d.getId()).forEach(System.out::println);
            list.stream()
                    .map(e->e.getId())
                    .filter(e->e!=1)
                    .map(id->newMerchInfoService.findOne(id)).forEach(
                    e->{
                        newMerchInfoService.update(e);
                    }
            );



    }

//    @Test
//    public void updatePath(){
//     // List<SysDeptEntity> list = sysDeptService.getSubDeptIdList(1l);
//
//
//
//            deptEntity.setPath(
//                    StringUtils.leftPad(deptEntity.getParentId().toString(),100,'0')
//                    +"-"
//                    +StringUtils.leftPad(deptEntity.getParentId().toString(),100,'0')
//            );
//
//            sysDeptService.update(deptEntity);
//       // }
//    }


}
