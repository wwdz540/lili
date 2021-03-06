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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    JdbcTemplate jdbcTemplate;

    /****/
    private WeakHashMap<Long,String> parenId2Path;


 //   @Test
    public void updateData(){
        jdbcTemplate.execute("delete from merchant_main where mc_id <> 1");
        List<Long> agenIds = jdbcTemplate.queryForList("select user_id from merch_info group by user_id",Long.class);//得到代理商ids
        agenIds.remove(1l);

        for (Long agenId : agenIds) {

            if(agenId == null) continue;
            SysUserEntity agen = userDao.findOne(agenId);

            SysDeptEntity dept = new SysDeptEntity();
            dept.setParentId(1l);
            dept.setName(agen.getUsername());
            dept.setDeptType(2);
            dept.setAddress("");
            dept.setIndustry("");
            dept.setLegalName("");

            sysDeptService.save(dept);
            jdbcTemplate.execute("update sys_user set mc_id = "+dept.getMcId() +" where user_id = "+agenId);
            update(dept.getMcId(),4,agen);
        }
        SysUserEntity u = new SysUserEntity();
        u.setUserId(1l);
        update(1l,1,u);

       // updatePath();
        jdbcTemplate.execute("update sys_user as u set u.mc_id = (select min( m.mc_id) from  merch_info m where m.merchno =u.username )\n" +
                "where u.mc_id = 10");

    }

   // @Test
    public void test2(){
        //(7,6,'力力数据',0,0),(8,6,'代理商',1,0),(9,8,'二级代理',0,0),(10,6,'商户',0,0);

        ArrayList waIds = new ArrayList();

        /**查询出来的代理*/
        waIds.add(4l);
        waIds.add(5l);
        waIds.add(7l);
        waIds.add(8l);

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
           entity.setMcId(dept.getMcId());
           userDao.update(entity);
           update(dept.getMcId(),4,entity);

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
            merch.setMcId(dept.getMcId());
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
        List<NewMerchInfoEntity> merchs = list.stream()
                .map(e -> e.getId())
                .filter(e -> e != 1)
                .map(id -> newMerchInfoService.findOne(id)).collect(Collectors.toList());


        String merchnoPrefix="AGENT_000_";

        for (int i = 0; i < merchs.size(); i++) {
            NewMerchInfoEntity e = merchs.get(i);
            if(e.getMerchno()== null){
                e.setMerchno(merchnoPrefix+i);

            }

            newMerchInfoService.update(e);
        }



    }

    @Test
  //  @Transactional
    public void updateDate4McIdIsNull(){
        List<Map<String, Object>> forUpdats = jdbcTemplate.queryForList("select * from merch_info where mc_id is null");


        for (Map<String, Object> forUpdat : forUpdats) {
            long willUpdateId = Long.parseLong(forUpdat.get("id").toString());

            long agenId = 1;
            Object userId = forUpdat.get("user_id");
            if(userId != null){
               Map<String,Object> userMap =  jdbcTemplate.queryForMap("select * from sys_user where user_id="+userId);

                agenId = Long.parseLong(userMap.get("mc_id").toString());

            }
            String agenPath;

            //获取代理信息
            Map<String, Object> am = jdbcTemplate.queryForMap("select * from merchant_main where mc_id=" + agenId);
            agenPath = am.get("path").toString();
         //   System.out.println(agenPath);
            System.out.println("agency is "+ am.get("name"));


            SysDeptEntity mc = new SysDeptEntity();
            mc.setName(forUpdat.get("merch_name").toString());
            if(forUpdat.get("legal_name")!=null)
            mc.setLegalName(forUpdat.get("legal_name").toString());
            mc.setParentId(agenId);
            mc.setParentName(am.get("name").toString());

            sysDeptService.save(mc);

            String path = agenPath +"-"+ StringUtils.leftPad(
                           mc.getMcId()+"",10,'0');
            mc.setPath(path);
            sysDeptService.update(mc);

            String updateSql = "update merch_info set mc_id= ? where id = "+willUpdateId;
            jdbcTemplate.update(updateSql,mc.getMcId());
        }

    }

//    @Test
//    public void updatePath(){
//     // List<SysDeptEntity> list = sysDeptService.getSubDeptIdList(1l);
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
