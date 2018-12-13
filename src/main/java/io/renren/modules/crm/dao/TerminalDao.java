package io.renren.modules.crm.dao;


import io.renren.modules.crm.entity.TerminalEntity;
import io.renren.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户信息
 *
 * @author zhuhui
 */
@Mapper
public interface TerminalDao extends BaseDao<TerminalEntity> {


	TerminalEntity findOne(int id);
}
