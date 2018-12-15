package io.renren.modules.crm.dao;


import io.renren.modules.crm.entity.RateConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RateConfigDao {
     void save(RateConfig config);
     int update(RateConfig config);
     RateConfig get(@Param("deptId")long deptId, @Param("payType") String payType);
}
