package io.renren.modules.crm.dao;


import io.renren.modules.crm.entity.MerchInfoEntity;
import io.renren.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商户信息
 *
 * @author zhuhui
 */
@Mapper
public interface MerchInfoDao extends BaseDao<MerchInfoEntity> {

	List<MerchInfoEntity> findAll();

	MerchInfoEntity findOne(int id);

	List<MerchInfoEntity> queryByUserId(@Param("list") List<Long> userIdList);
}
