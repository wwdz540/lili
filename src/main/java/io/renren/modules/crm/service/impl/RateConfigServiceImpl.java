package io.renren.modules.crm.service.impl;

import io.renren.modules.crm.dao.RateConfigDao;
import io.renren.modules.crm.entity.RateConfig;
import io.renren.modules.crm.service.RateConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RateConfigServiceImpl implements RateConfigService {

    @Autowired
   private RateConfigDao rateConfigDao;
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(RateConfig rateConfig) {
        RateConfig mconfig =rateConfigDao.get(rateConfig.getDeptId(),rateConfig.getPayType());
        if(mconfig == null){
            rateConfigDao.save(rateConfig);
        }else {
            rateConfigDao.update(rateConfig);
        }
    }

    @Override
    public List<RateConfig> findByDept(Long id) {
        return rateConfigDao.findByDept(id);
    }
}
