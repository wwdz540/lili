package io.renren.modules.crm.service.impl;

import io.renren.modules.crm.dao.NewMerchInfoDao;
import io.renren.modules.crm.entity.MerchInfoEntity;
import io.renren.modules.crm.entity.NewMerchInfoEntity;
import io.renren.modules.crm.service.MerchInfoService;
import io.renren.modules.crm.service.NewMerchInfoService;
import io.renren.modules.sys.entity.SysDeptEntity;
import io.renren.modules.sys.service.SysDeptService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class NewMerchInfoServiceImpl implements NewMerchInfoService {

    @Autowired
    private MerchInfoService merchInfoService;

    @Autowired
    private SysDeptService deptService;

    @Autowired
    private NewMerchInfoDao newMerchInfoDao;

    @Override
    @Transactional
    public void save(NewMerchInfoEntity entity) {
        SysDeptEntity dept = entity.getDept();
        MerchInfoEntity merch = entity.getMerchInfo();
        /*1.添加部门**/
        deptService.save(dept);


       if(StringUtils.isNotBlank(merch.getMerchno())) {
           /*2.将部门id写到商户中**/
           merch.setDeptId(dept.getDeptId());
           /*3.添加商户*/
           merchInfoService.save(merch);
       }




    }

    @Override
    public void update(NewMerchInfoEntity merchInfoEntity) {

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

    private void fillParentName(List<NewMerchInfoEntity> list){
        SysDeptEntity dept;
        for (NewMerchInfoEntity entity : list) {
             dept = deptService.queryObject(entity.getParentId());
             if(dept!=null)
                entity.setParentName(dept.getName());
        }
    }
}
