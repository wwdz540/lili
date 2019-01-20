package io.renren.modules.crm.dao;

import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ITransDataDao extends BaseDao<TransDataEntity> {

    /**
     * select ${gfield},${collection} from table
     * group by ${gfield}
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> queryByGroup(Map<String, Object> params);

    void updateShareBenefit(@Param("id") long id,@Param("benefit") double benefit);
}