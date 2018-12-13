package io.renren.modules.crm.service;

import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.sys.entity.SysUserEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TransDataService {

    List<TransDataEntity> queryList(Map<String, Object> map);

    BigDecimal groupByCardType(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    TransDataEntity findOne(int id);

    Map<String,Object> countTransData();

    Map<String,Object> transData();

    Map<String,Object> getSumData(Map<String, Object> map, SysUserEntity userEntity);
}
