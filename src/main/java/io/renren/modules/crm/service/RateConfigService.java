package io.renren.modules.crm.service;

import io.renren.modules.crm.entity.RateConfig;

import java.util.List;

public interface RateConfigService {
    void saveOrUpdate(RateConfig rateConfig);

    List<RateConfig> findByDept(Long id);
}
