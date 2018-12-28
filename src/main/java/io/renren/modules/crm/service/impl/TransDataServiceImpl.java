package io.renren.modules.crm.service.impl;

import io.renren.common.utils.CommonUtil;
import io.renren.common.utils.DateUtils;
import io.renren.modules.crm.dao.MerchInfoDao;
import io.renren.modules.crm.dao.TerminalDao;
import io.renren.modules.crm.dao.TransDataDao;
import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.model.GroupByCardType;
import io.renren.modules.crm.service.TransDataService;
import io.renren.modules.crm.utils.TypeUtils;
import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

@Service("transDataService")
public class TransDataServiceImpl implements TransDataService{
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    @Autowired
    private TransDataDao transDataDao;

    @Autowired
    private MerchInfoDao merchInfoDao;

    @Autowired
    private TerminalDao terminalDao;

    @Autowired
    private UserGetService userGetService;

    @Override
    public List<TransDataEntity> queryList(Map<String, Object> map) {
        List<TransDataEntity> list = transDataDao.queryList(map);
        for (TransDataEntity entity : list){
            if ("00".equals(entity.getRespCode())){
                entity.setRespCode("交易成功");
            }else {
                entity.setRespCode("交易失败");
            }
            entity.setMerchName(StringUtils.isBlank(entity.getMerchName()) ? "未录入商户" : entity.getMerchName());
            BigDecimal amt = new BigDecimal(entity.getAmt()).divide(BigDecimal.valueOf(100),2,BigDecimal.ROUND_HALF_UP);
            entity.setAmt(String.valueOf(amt));

            //分润
            if(entity.getShareBenefit()!=null) {
                entity.setSharePoint(BigDecimal.valueOf(entity.getShareBenefit()).divide(ONE_HUNDRED, 2, BigDecimal.ROUND_HALF_UP));
            }

            entity.setCardType(TypeUtils.cardType(entity.getCardType()));
            entity.setIssuerCode(TypeUtils.payType(entity.getIssuerCode()));
        }
        return list;
    }

    @Override
    public BigDecimal groupByCardType(Map<String, Object> map) {
        List<GroupByCardType> list = transDataDao.groupByCardType(map);
        BigDecimal amount = BigDecimal.ZERO;
        if (CollectionUtils.isEmpty(list)){
            return amount;
        }
        for (GroupByCardType group : list){
            BigDecimal amt = new BigDecimal(group.getAmt());

            BigDecimal decimal = CommonUtil.sharePoint(amt, group.getCardType());

            amount = amount.add(decimal);
        }
        amount = amount.setScale(2,BigDecimal.ROUND_HALF_UP);
        return amount;
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return transDataDao.queryTotal(map);
    }

    @Override
    public TransDataEntity findOne(int id) {
        return transDataDao.findOne(id);
    }

    @Override
    public Map<String, Object> countTransData() {
        Map<String,Object> query = new HashMap<>();
        query.put("date", DateUtils.getStartTime());
        if (!CommonUtil.isAdmin()){
            query.put("userIdList",userGetService.getUserId(CommonUtil.getUser().getUserId()));
        }
        List<TransDataEntity> list = transDataDao.queryTodayData(query);
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal sharePoint = BigDecimal.ZERO;

        for (TransDataEntity entity : list){
            BigDecimal amt = new BigDecimal(entity.getAmt()).divide(BigDecimal.valueOf(100));
            amount = amount.add(amt);

            //分润计算
            BigDecimal sp = CommonUtil.sharePoint(amt,entity.getCardType());
            sharePoint = sharePoint.add(sp);
        }

        //查询商户 和终端数量
        Map<String,Object> map = new HashMap<>();
        if (!CommonUtil.isAdmin()){
            map.put("userIdList",userGetService.getUserId(CommonUtil.getUser().getUserId()));
        }
        int merchTotal = merchInfoDao.queryTotal(map);
        int terminalTotal = terminalDao.queryTotal(map);

        Map<String,Object> data = new HashMap<>();
        data.put("amount",amount);
        data.put("sharePoint",sharePoint);
        data.put("count",list.size());
        data.put("num",merchTotal+"/"+terminalTotal);
        return data;
    }

    @Override
    public Map<String, Object> transData() {

        Map<String,Object> data = new HashMap<>();


        List<String> timeList = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>();

        Date date = new Date();
        for (int i = 7 ; i >= 1; i --){
            Date currTime = DateUtils.getNextSomeDay(date, i);
            Date dateStart = DateUtils.getStartTime(currTime);
            Date dateEnd = DateUtils.getnowEndTime(currTime);

            Map<String,Object> query = new HashMap<>();
            query.put("dateStart",dateStart);
            query.put("dateEnd",dateEnd);

            List<TransDataEntity> list = transDataDao.queryList(query);


            BigDecimal amount = BigDecimal.ZERO;
            for (TransDataEntity entity: list){
                BigDecimal amt = new BigDecimal(entity.getAmt()).divide(BigDecimal.valueOf(100));
                amount = amount.add(amt);
            }

            timeList.add(DateUtils.format(currTime, DateUtils.DATE_PATTERN));
            amountList.add(amount);
        }

        data.put("transTime",timeList);
        data.put("amount",amountList);
        return data;
    }

    @Override
    public Map<String, Object> getSumData(Map<String, Object> map, SysUserEntity userEntity) {
        List<TransDataEntity> list = transDataDao.queryList(map);

        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal sharePoint = BigDecimal.ZERO;
        for (TransDataEntity entity : list){
            BigDecimal amt = new BigDecimal(entity.getAmt()).divide(BigDecimal.valueOf(100),2,BigDecimal.ROUND_HALF_UP);
            amount = amount.add(amt);
            //分润计算
            BigDecimal sp = BigDecimal.valueOf(entity.getShareBenefit()); //CommonUtil.sharePoint(amt,entity.getCardType(),userEntity);
            sharePoint = sharePoint.add(sp);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("amount",amount);//总金额
        result.put("sharePoint",sharePoint.divide(ONE_HUNDRED,2,BigDecimal.ROUND_HALF_UP)); //总分润
        return result;
    }

    @Override
    public int queryAvg(Map<String, Object> map) {
        Integer avg =  transDataDao.queryAvg(map);
        return avg == null ? 0 : avg;
    }


}
