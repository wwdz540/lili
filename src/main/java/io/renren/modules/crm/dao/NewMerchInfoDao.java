package io.renren.modules.crm.dao;

import io.renren.modules.crm.entity.NewMerchInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NewMerchInfoDao {
    List<NewMerchInfoEntity> queryList(Map<String,Object> map);
    int queryTotal(Map<String,Object> map);
}
