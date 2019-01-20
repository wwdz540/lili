package io.renren.modules.crm.service.impl;

import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.service.ITransDataService;
import org.apache.shiro.util.SoftHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.util.*;

/**
 * 用来修正
 */
public  class ShareTransDataServerImpl {
    private static final Logger log = LoggerFactory.getLogger(ShareTransDataServerImpl.class);

    //@Autowired
    private ITransDataService transDataService;

    private JdbcTemplate jdbcTemplate;

    public void setTransDataService(ITransDataService transDataService) {
        this.transDataService = transDataService;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void fixSharePoin(String path, Date start, Date end) throws SharePointException{
        merchCache.clear();
        merchCache4ID.clear();
        configCache.clear();

        Map<String,Object> param  = new HashMap<>();

        param.put("path",path);
        param.put("dateStart",start);
        param.put("dateEnd",end);
        List<TransDataEntity> datas = transDataService.queryList(param);
        for (TransDataEntity data : datas) {
            fixSharePoint(data);
        }
    }

    public void fixSharePoint(TransDataEntity trans) throws SharePointException {
        log.info("trandata is {}",trans);
        BigDecimal bigDecimal = getSharePoint(
                trans.getMerchantId(),
                Double.valueOf(trans.getAmt()),
                trans.getIssuerCode(),
                trans.getCardType());

       double share = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP)
                .doubleValue();
       // trans.setShareBenefit(share);

        if(trans.getShareBenefit() ==null || share != trans.getShareBenefit()) {
            transDataService.updateSharePoint(trans.getId().longValue(), share);
        }
    }



    private BigDecimal getSharePoint(String merchNo, double money, String payType, String cardType) throws SharePointException {
        String paymehtod = getPayment(payType,cardType);

        log.info("pay method is {}",paymehtod);

        //查询商户
        Merch merch = getMerch(merchNo);

       log.info("merch is {}",merch);
        if(merch == null){
            throw new SharePointException("商铺为空");
        }
//       else if(merch.getParentId() == 1){
//            throw new SharePointException("你商铺为1不可以有");
//        }
        Merch parent = getMerchByteId(merch.getParentId());
        log.info("parent is {}",parent);

        if(parent == null || parent.getType()!=2){ //如果父商户不是代理商
            return BigDecimal.ZERO;
        }
        RateConfig merchRateConfig = getRateConfig(merch.getId(),paymehtod);

        log.info("merch rate config is {}",merchRateConfig);
        RateConfig agencyRateConfig = getRateConfig(parent.getId(),paymehtod);
        log.info("age rate  config is {}",agencyRateConfig);

        //查询商户底下有无费率

        if(merchRateConfig==null || agencyRateConfig==null)
        {
            log.warn("merchRateConfig or agencyRateconfig is null,merch is {},parent is {}",merch,parent);
            //原设制
            BigDecimal result =  BigDecimal.valueOf(computeMoney(money,paymehtod));

            return result;
        }

      //  RateConfig merchRateConfig = getRateConfig(merch.getId(),paymehtod);



        BigDecimal rate;
        if(merchRateConfig.getRate().doubleValue() > agencyRateConfig.getRate().doubleValue()){
            rate = merchRateConfig.getRate().subtract(agencyRateConfig.getRate());
            log.info("get the rate  with normal");
        }else{
            rate = merchRateConfig.getBenefit();
            log.info("get the rate  with benefit");
        }

        log.info("rate is {}",rate);


        BigDecimal price = new BigDecimal(money);
        BigDecimal resultMony = price
                .divide(new BigDecimal(100))
                .multiply(rate);

        log.info("{}*{}%={}",price,rate,resultMony);

        if(merchRateConfig.getMax().doubleValue() > 0 && resultMony.doubleValue() > merchRateConfig.getMax().doubleValue()){
            resultMony = merchRateConfig.getMax();
        }

        return resultMony;
    }


    /**
     * 手续费计算
     * @param money
     * @param cardType
     * @return
     */
    private Double computeMoney(Double money, String cardType){
        //判断支付类型
        Double chargeMoney = 0D;
        if ("贷记卡".equals(cardType)){
            chargeMoney = money / 1000 * 6 ;
            return chargeMoney;
        }

        if ("借记卡".equals(cardType)){
            chargeMoney = money / 1000 * 5 ;
            if (chargeMoney.compareTo(Double.valueOf(25)) >0){
                return Double.valueOf(25);
            }else{
                return chargeMoney;
            }
        }

        if ("扫码".equals(cardType)){
            chargeMoney = money / 1000 * 3.8 ;
            return chargeMoney;
        }

        return chargeMoney;
    }


    private Map<String,RateConfig> configCache = new WeakHashMap<>();

    private RateConfig getRateConfig(Long id,String paymethod){

         RateConfig rateConfig = configCache.get(id+paymethod);

         if(rateConfig==null) {

             String sql = "select * from rate_config as rc where rc.mc_id = " + id
                     + " and rc.pay_type='" + paymethod + "'";
             System.out.println(sql);

             List<RateConfig> mlist = jdbcTemplate.query(sql, rm);
             if (mlist.size() > 0)
                 rateConfig = mlist.get(0);

             if (rateConfig == null) { //如果没有查到,付款方式为标准
                 sql = "select * from rate_config as rc where rc.mc_id = " + id
                         + " and rc.pay_type='标准'";
                 mlist = jdbcTemplate.query(sql, rm);
                 if (mlist.size() > 0)
                     rateConfig = mlist.get(0);

             }
             configCache.put(id+paymethod,rateConfig);
         }
        return rateConfig;
    }




    private String getPayment(String payType,String cardType){
        switch (payType){
            case "支付宝":
            case "微信" :
                return payType;
            case "银行卡":
                return cardType;
        }
        return "标准";
    }


    private Map<String,Merch> merchCache= new WeakHashMap<>();



    private Merch getMerch(String merchNo) {

        Merch m = merchCache.get(merchNo);
        if (m == null){
            String sql= "select\n" +
                    "  d.mc_id as id,d.parent_id ,d.name,d.dept_type as type\n" +
                    "from merch_info m left join merchant_main d on m.mc_id = d.mc_id\n" +
                    "where merchno = '" + merchNo + "'";

        m = jdbcTemplate.queryForObject(sql,merchRowMapper);


        if(m.getType() == 5){ //如果是集团公司子帐户
                m = getMerchByteId(m.getParentId());
        }
        merchCache.put(merchNo,m);
    }
        return m;
    }

    private Map<Long,Merch> merchCache4ID = new SoftHashMap<>();

    private Merch getMerchByteId(long id){

        Merch m = merchCache4ID.get(id);
        if(m==null) {
            String sql = "select\n" +
                    "  d.mc_id as id,d.parent_id ,d.name,d.dept_type as type\n" +
                    "from merch_info m left join merchant_main d on m.mc_id = d.mc_id\n" +
                    "where d.mc_id = " + id + "";
            m = jdbcTemplate.queryForObject(sql,merchRowMapper);
            merchCache4ID.put(id,m);
        }
        return m;
    }




    protected static class RateConfig{

        private BigDecimal rate ; //费率
        private BigDecimal benefit; //分润
        private BigDecimal max; //最高

        public BigDecimal getRate() {
            return rate;
        }

        public void setRate(BigDecimal rate) {
            this.rate = rate;
        }

        public BigDecimal getBenefit() {
            return benefit;
        }

        public void setBenefit(BigDecimal benefit) {
            this.benefit = benefit;
        }

        public BigDecimal getMax() {
            return max;
        }

        public void setMax(BigDecimal max) {
            this.max = max;
        }

        @Override
        public String toString() {
            return "RateConfig{" +
                    "rate=" + rate +
                    ", benefit=" + benefit +
                    ", max=" + max +
                    '}';
        }
    }

    protected static class Merch{
        private String name;
        private long id;
        private long parentId;
        private int type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getParentId() {
            return parentId;
        }

        public void setParentId(long parentId) {
            this.parentId = parentId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Merch{" +
                    "name='" + name + '\'' +
                    ", id=" + id +
                    ", parentId=" + parentId +
                    ", type=" + type +
                    '}';
        }
    }

    private RowMapper<Merch> merchRowMapper = (rs,idx)->{
        Merch m = new Merch();
        m.setId(rs.getLong("id"));
        m.setParentId(rs.getLong("parent_id"));
        m.setType(rs.getInt("type"));
        m.setName(rs.getString("name"));
        return m;
    };

   private RowMapper<RateConfig> rm = (rs,idx)-> {

       System.out.println("config maping");
            RateConfig rateConfig = new RateConfig();
            rateConfig.setBenefit(new BigDecimal(rs.getString("share_benefit")));
            rateConfig.setRate(new BigDecimal(rs.getString("rate")));
            rateConfig.setMax(new BigDecimal(rs.getString("max")));
            return rateConfig;
        };

    public class SharePointException extends Exception {
        public SharePointException(String s) {
            super(s);
        }
    }
}
