package io.renren.modules.crm.service;

import io.renren.modules.crm.entity.MerchInfoEntity;

import java.util.List;
import java.util.Map;

public interface MerchInfoService {


    List<MerchInfoEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(MerchInfoEntity merchInfoEntity);

    void update(MerchInfoEntity merchInfoEntity);

    void deleteBatch(Integer[] ids);

    List<MerchInfoEntity> queryByUserId(List<Long> userIdList);

    List<MerchInfoEntity> findAll();

    MerchInfoEntity findOne(int id);

}
