package io.renren.modules.crm.service;


import io.renren.modules.crm.entity.NewMerchInfoEntity;

import java.util.List;
import java.util.Map;

public interface NewMerchInfoService {

    void save(NewMerchInfoEntity merchInfoEntity);

    void update(NewMerchInfoEntity merchInfoEntity);

    List<NewMerchInfoEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    NewMerchInfoEntity findOne(long merchId);
}
