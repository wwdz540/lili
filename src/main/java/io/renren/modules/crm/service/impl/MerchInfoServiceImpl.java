package io.renren.modules.crm.service.impl;

import io.renren.modules.crm.dao.MerchInfoDao;
import io.renren.modules.crm.entity.MerchInfoEntity;
import io.renren.modules.crm.service.MerchInfoService;
import io.renren.modules.sys.dao.SysUserDao;
import io.renren.modules.sys.dao.SysUserRoleDao;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("merchInfoService")
public class MerchInfoServiceImpl implements MerchInfoService {

    @Autowired
    private MerchInfoDao merchInfoDao;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysUserRoleDao sysUserRoleDao;


    @Override
    public List<MerchInfoEntity> queryList(Map<String, Object> map) {
        List<MerchInfoEntity> list = merchInfoDao.queryList(map);

//        for (MerchInfoEntity entity : list){
//            SysUserEntity userEntity = sysUserDao.findOne(entity.getUserId());
//            entity.setUsername(userEntity.getUsername());
//        }
        return list;
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return merchInfoDao.queryTotal(map);
    }

    @Override
    public void save(MerchInfoEntity merchInfoEntity) {
        merchInfoDao.save(merchInfoEntity);
        try {
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
            user.setDeptId(10L);
            sysUserDao.save(user);


            Map<String, Object> map = new HashMap<>();
            map.put("userId", user.getUserId());
            map.put("roleIdList", Lists.newArrayList(8L));
            sysUserRoleDao.save(map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(MerchInfoEntity merchInfoEntity) {
        merchInfoDao.update(merchInfoEntity);
    }

    @Override
    public void deleteBatch(Integer[] ids) {
        merchInfoDao.deleteBatch(ids);
    }

    @Override
    public List<MerchInfoEntity> queryByUserId(List<Long> userIdList) {
        try {
            return merchInfoDao.queryByUserId(userIdList);
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    @Override
    public List<MerchInfoEntity> findAll() {
        return merchInfoDao.findAll();
    }

    @Override
    public MerchInfoEntity findOne(int id) {
        MerchInfoEntity merchInfoEntity = merchInfoDao.findOne(id);
        SysUserEntity userEntity = sysUserDao.findOne(merchInfoEntity.getUserId());
        merchInfoEntity.setUsername(userEntity.getUsername());
        return merchInfoEntity;
    }
}
