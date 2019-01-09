package io.renren.modules.crm.dao;


import io.renren.modules.crm.entity.RateConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RateConfigDao {
     void save(RateConfig config);
     int update(RateConfig config);
     RateConfig get(@Param("mcId")long mcId, @Param("payType") String payType);
     List<RateConfig> findByDept(Long deptId);
}
