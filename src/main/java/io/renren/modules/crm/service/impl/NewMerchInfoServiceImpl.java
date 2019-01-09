package io.renren.modules.crm.service.impl;

import io.renren.common.exception.RRException;
import io.renren.modules.crm.dao.MerchInfoDao;
import io.renren.modules.crm.dao.NewMerchInfoDao;
import io.renren.modules.crm.entity.MerchInfoEntity;
import io.renren.modules.crm.entity.NewMerchInfoEntity;
import io.renren.modules.crm.entity.RateConfig;
import io.renren.modules.crm.service.NewMerchInfoService;
import io.renren.modules.crm.service.RateConfigService;
import io.renren.modules.sys.dao.SysUserDao;
import io.renren.modules.sys.entity.SysDeptEntity;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysDeptService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class NewMerchInfoServiceImpl implements NewMerchInfoService {

    @Autowired
    private MerchInfoDao merchInfoDao;

    @Autowired
    private SysDeptService deptService;

    @Autowired
    private NewMerchInfoDao newMerchInfoDao;

    @Autowired
    private RateConfigService rateConfigService;

    @Autowired
    private SysUserDao userDao;




    @Override
    @Transactional
    public void save(NewMerchInfoEntity entity) {
        SysDeptEntity dept = entity.getDept();
        MerchInfoEntity merch = entity.getMerchInfo();
        /*1.添加部门**/

        deptService.save(dept);

        fillPath(dept);
        deptService.update(dept);


        if(entity.getRateConfigs()!=null){
            for (RateConfig config : entity.getRateConfigs()) {
                config.setMcId(dept.getMcId());
                rateConfigService.saveOrUpdate(config);
            }
        }

       if(StringUtils.isNotBlank(merch.getMerchno())) {
           /*2.将部门id写到商户中**/
           merch.setMcId(dept.getMcId());
           /*3.添加商户*/
           merchInfoDao.save(merch);
       }
       if(entity.getDeptType() == 1 || entity.getDeptType() == 4 || entity.getDeptType() ==5){
            saveUser(merch);
       }
    }

    /***
     * 添加类别为商户时，自动添加用户

     */
    private void saveUser(MerchInfoEntity merchInfoEntity){
        SysUserEntity user = new SysUserEntity();
        user.setCreateTime(new Date());
        user.setCreateUserId(1L);
        user.setLeader(1L);
        user.setUsername(merchInfoEntity.getMerchno());
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setPassword("88888888");
        user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
        user.setSalt(salt);

        user.setEmail("1@qq.com");
        user.setStatus(1);
        user.setMcId(merchInfoEntity.getMcId());
        userDao.save(user);
    }

    @Override
    public void update(NewMerchInfoEntity merchInfoEntity) {

        MerchInfoEntity merch = merchInfoEntity.getMerchInfo();
        if(merch.getId() != null ) {
            merchInfoDao.update(merch);
        }else{
            merchInfoDao.save(merch);
        }

        SysDeptEntity dept = merchInfoEntity.getDept();
        fillPath(dept);

        deptService.update(dept);
        //merchInfoEntity.getRateConfigs().forEach(System.out::println);

        for (RateConfig rateConfig : merchInfoEntity.getRateConfigs()) {
            rateConfig.setMcId(dept.getMcId());
            rateConfigService.saveOrUpdate(rateConfig);
        }
    }

    @Override
    public List<NewMerchInfoEntity> queryList(Map<String, Object> map) {
        List<NewMerchInfoEntity> list = newMerchInfoDao.queryList(map);
        fillParentName(list);
        return list;
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return newMerchInfoDao.queryTotal(map);
    }

    @Override
    public NewMerchInfoEntity findOne(long merchId) {
        NewMerchInfoEntity newMerchInfoEntity = newMerchInfoDao.get(merchId);
        if(newMerchInfoEntity!=null){
            List<RateConfig> list = rateConfigService.findByDept(newMerchInfoEntity.getId());
            newMerchInfoEntity.setRateConfigs(list);
        }
        return newMerchInfoEntity;

    }

    @Override
    @Transactional
    public void deleteBatch(Integer[] merchIds) {

        //查看合法性
        for (Integer merchId : merchIds) {
            Long id =  merchId.longValue();
            Map<String,Object> paras = new HashMap<>();
            paras.put("parentId",id);
            int count = newMerchInfoDao.queryTotal(paras);
            if(count>0){
                throw new RRException("不可删除，当前商铺存在子商铺!");
            }
        }
        for (Integer merchId : merchIds) {
            deptService.delete(merchId.longValue());
        }
    }

    private void fillParentName(List<NewMerchInfoEntity> list){
        SysDeptEntity dept;
        for (NewMerchInfoEntity entity : list) {
            //@TODO 临时path解决方案

            String preString ="";

           int c= entity.getPath() ==null ?  0 : entity.getPath().split("-").length;
           for(int i=0;i<c;i++)
            {
                preString += "├";
            }

            entity.setName(preString + entity.getName());

             dept = deptService.queryObject(entity.getParentId());
             if(dept!=null)
                entity.setParentName(dept.getName());
        }
    }

    private void fillPath(SysDeptEntity merchInfoEntity){
        SysDeptEntity parent = deptService.queryObject(merchInfoEntity.getParentId());


        String path = parent.getPath() +"-"+ StringUtils.leftPad(
                merchInfoEntity.getMcId()+"",10,'0');


        merchInfoEntity.setPath(path);
    }


}
