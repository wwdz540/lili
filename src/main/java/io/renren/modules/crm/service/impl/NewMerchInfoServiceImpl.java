package io.renren.modules.crm.service.impl;

import io.renren.common.exception.RRException;
import io.renren.modules.crm.dao.MerchInfoDao;
import io.renren.modules.crm.dao.NewMerchInfoDao;
import io.renren.modules.crm.entity.MerchInfoEntity;
import io.renren.modules.crm.entity.NewMerchInfoEntity;
import io.renren.modules.crm.entity.RateConfig;
import io.renren.modules.crm.service.MerchInfoService;
import io.renren.modules.crm.service.NewMerchInfoService;
import io.renren.modules.crm.service.RateConfigService;
import io.renren.modules.sys.entity.SysDeptEntity;
import io.renren.modules.sys.service.SysDeptService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

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
                config.setDeptId(dept.getDeptId());
                rateConfigService.saveOrUpdate(config);
            }
        }

       if(StringUtils.isNotBlank(merch.getMerchno())) {
           /*2.将部门id写到商户中**/
           merch.setDeptId(dept.getDeptId());
           /*3.添加商户*/
           merchInfoDao.save(merch);
       }


    }

    @Override
    public void update(NewMerchInfoEntity merchInfoEntity) {

        merchInfoDao.update(merchInfoEntity.getMerchInfo());

        SysDeptEntity dept = merchInfoEntity.getDept();
        fillPath(dept);

        System.out.println("================");
        System.out.println(dept.getPath());

        deptService.update(dept);
        for (RateConfig rateConfig : merchInfoEntity.getRateConfigs()) {
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
                merchInfoEntity.getDeptId()+"",10,'0');


        merchInfoEntity.setPath(path);
    }


}
