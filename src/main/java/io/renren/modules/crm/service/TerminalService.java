package io.renren.modules.crm.service;

import io.renren.modules.crm.entity.TerminalEntity;

import java.util.List;
import java.util.Map;

public interface TerminalService {

    List<TerminalEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(TerminalEntity terminalEntity);

    void update(TerminalEntity terminalEntity);

    void deleteBatch(Integer[] ids);

    TerminalEntity findOne(int id);
}
