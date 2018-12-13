package io.renren.modules.crm.dao;


import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.model.GroupByCardType;
import io.renren.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 商户信息
 *
 * @author zhuhui
 */
@Mapper
public interface TransDataDao extends BaseDao<TransDataEntity> {

	TransDataEntity findOne(int id);

	List<TransDataEntity> queryTodayData(Map<String, Object> map);

	List<GroupByCardType> groupByCardType(Map<String, Object> map);
}
