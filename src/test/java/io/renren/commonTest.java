package io.renren;


import com.alibaba.fastjson.JSON;
import io.renren.datasources.DataSourceTestService;
import io.renren.modules.api.entity.UserEntity;
import io.renren.modules.crm.entity.MerchInfoEntity;
import io.renren.modules.crm.service.MerchInfoService;
import io.renren.modules.sys.dao.SysUserDao;
import io.renren.modules.sys.dao.SysUserRoleDao;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class commonTest {
    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private MerchInfoService merchInfoService;

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Test
    public void test(){
        List<MerchInfoEntity> merchInfoEntities = merchInfoService.findAll();

        for (MerchInfoEntity merchInfoEntity : merchInfoEntities) {
            SysUserEntity user = new SysUserEntity();
            try {
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
            }catch (Exception e){
                System.out.println("商户号："+merchInfoEntity.getMerchno());
            }
        }
    }

    @Test
    public void test1(){

        List<SysUserEntity> list = sysUserDao.findAll();
        for (SysUserEntity userEntity : list) {
            if (userEntity.getDeptId() != 10){
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userEntity.getUserId());
            map.put("roleIdList", Lists.newArrayList(8L));
            sysUserRoleDao.save(map);
        }
    }

}
