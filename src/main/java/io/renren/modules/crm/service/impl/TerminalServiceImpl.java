package io.renren.modules.crm.service.impl;

import io.renren.modules.crm.dao.TerminalDao;
import io.renren.modules.crm.entity.TerminalEntity;
import io.renren.modules.crm.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("terminalService")
public class TerminalServiceImpl implements TerminalService{

    @Autowired
    private TerminalDao terminalDao;


    @Override
    public List<TerminalEntity> queryList(Map<String, Object> map) {

        return terminalDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return terminalDao.queryTotal(map);
    }

    @Override
    public void save(TerminalEntity terminalEntity) {
        terminalDao.save(terminalEntity);
    }

    @Override
    public void update(TerminalEntity terminalEntity) {
        terminalDao.update(terminalEntity);
    }

    @Override
    public void deleteBatch(Integer[] ids) {
        terminalDao.deleteBatch(ids);
    }

    @Override
    public TerminalEntity findOne(int id) {
        return terminalDao.findOne(id);
    }
}
