package io.renren.modules.crm.controller;

import io.renren.common.utils.CommonUtil;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.crm.entity.NewMerchInfoEntity;
import io.renren.modules.crm.service.ITransDataService;
import io.renren.modules.crm.service.NewMerchInfoService;
import io.renren.modules.crm.service.impl.ShareTransDataServerImpl;
import io.renren.modules.sys.service.SysDeptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * @Author wangzhipig
 * 这个添是我新加的，用来做查询分析之用。若用mybitis 做查询分析实在太浪时间，也不能发挥mybatis的优势
 */
@RestController
@RequestMapping("/crm/stc")
public class StatisticController  extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(StatisticController.class);

    @Autowired
    private SysDeptService deptService;

    @Autowired
    private NewMerchInfoService merchInfoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String,Object> params){
        fixParas(params);
        DateUtils.fixQueryDate(params);
        ITransDataService service = getService(params);

        NewMerchInfoEntity merch = getCurrentMerch();
        params.put("deptType",2);
        List<NewMerchInfoEntity> list = merchInfoService.queryList(params);
        List<Map<String,Object>> result = new ArrayList<>();
        params.remove("deptType");
        for (NewMerchInfoEntity m : list) {

            params.put("path",m.getPath());
            Map<String,Object> item = new HashMap<>();
            item.put("name",m.getName());
           log.info("merch name is {}",m.getName());

            Map<String, Object> summary = service.createGroupQuery(params)
                    .collection4Amt("count", "count")
                    .collection4Amt("sum", "sum")
                    .collection4Amt("avg", "avg")
                    .collection4Share("sum", "sharePoint")
                    .querySingleMap();
            item.putAll(summary);
            result.add(item);
        }

        return R.ok().put("result",result);
    }

    @RequestMapping("/summary")
    public R summaryByMonth(@RequestParam Map<String,Object> params){
        fixParas(params);
        List<Map<String,Object>> result =
        monthOfYears()
                .map(start -> summaryByMonth(start.getYear(),start.getMonthValue(),params))
                .collect(Collectors.toList());
        return R.ok().put("result",result);
    }


    private Map<String,Object> summaryByMonth(int year,int month,
                                             Map<String,Object> params){
        ITransDataService service = getService(params);

        params.put("dateStart",year+"-"+month+"-"+"1");
        params.put("dateEnd",year+"-"+month+"-"+"31 23:59:59");

        Map<String,Object> summary= service
                .createGroupQuery(params)
                .collection4Amt("count","count")
                .collection4Amt("sum","amt-sum")
                .collection4Amt("avg","amt-avg")
                .collection4Share("sum","share-sum")
                .collection4Share("avg","share-avg").querySingleMap();
        summary.put("amt-sum",CommonUtil.formatAB(summary.get("amt-sum")));
        summary.put("amt-avg",CommonUtil.formatAB(summary.get("amt-avg")));
        summary.put("share-sum",CommonUtil.formatAB(summary.get("share-sum")));
        summary.put("share-avg",CommonUtil.formatAB(summary.get("share-avg")));

        summary.put("month",year+"-"+month+"月");
        return summary;

    }

    /***
     * 支付方式统计
     * @param params
     * @return
     */
    @RequestMapping("/summary4payType")
    private R summaryByMonth4PayType(@RequestParam Map<String,Object> params){
        fixParas(params);


        String[] payTypes=new String[]{"微信","支付宝","借记卡","贷记卡"};

        List<Map<String,String>> result = monthOfYears().map(date ->{
             Map<String,String> item = new HashMap<>();
            item.put("month",date.getYear()+"-"+date.getMonthValue()+"月");
            for (String payType : payTypes) {
                params.put(ITransDataService.QUERY_PAY_TYPE,payType);
                item.put(payType,CommonUtil.formatAB(summaryByMonth2(date.getYear(),date.getMonthValue(),params)));
            }
           return item;
        }).collect(Collectors.toList());
        return R.ok().put("result",result);
    }

    private Object summaryByMonth2(int year,int month,
                                               Map<String,Object> params){
        ITransDataService service = getService(params);

        params.put("dateStart",year+"-"+month+"-"+"1");
        params.put("dateEnd",year+"-"+month+"-"+"31 23:59:59");
       return service.createGroupQuery(params)
               .collection4Amt("sum","sum")
                .querySingleObject();
    }

    /**首页数据显示*/
    @RequestMapping("/countTransData")
    public R countTransData(@RequestParam Map<String,Object> params) {
        fixParas(params);
        ITransDataService service = getService(params);
        NewMerchInfoEntity crtMerch = getCurrentMerch();

        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String endDate = df.format(cal.getTime());
        cal.add(Calendar.DATE,-1);
        String startDate =  df.format(cal.getTime());
        params.put("dateStart",startDate);
        params.put("dateEnd",endDate);
        DateUtils.fixQueryDate(params);


        ITransDataService.GroupQuery groupQuery = service
                .createGroupQuery(params)
                .collection4Amt("count", "count")
                .collection4Amt("sum", "amt-sum")
                .collection4Amt("avg", "amt-avg");
        if(crtMerch.getDeptType()==2 || crtMerch.getDeptType()==0){
           groupQuery.collection4Share("sum","share-sum")
                    .collection4Share("avg","share-avg");
        }
       Map<String,Object> summary = groupQuery.querySingleMap();

//        Map<String,Object> data = new HashMap<>();
//        data.put("amount",amount);
//        data.put("sharePoint",sharePoint);
//        data.put("count",list.size());
//        data.put("num",merchTotal+"/"+terminalTotal);
        String path = params.get("path")==null ? "00000000":params.get("path").toString();
        String merchCountSql="select count(1) from merchant_main as dept left join merch_info as merch on merch.mc_id= dept.mc_id\n" +
                "        where dept.del_flag=0  and dept.path like '"+path+"%' and dept.dept_type in(1,3,5)";

        int merchTotal = jdbcTemplate.queryForObject(merchCountSql,Integer.class);

        String terminalCountSql ="select count(1) from terminal t\n" +
                "LEFT JOIN merch_info m ON t.merch_id = m.id" +
                " Left JOIN merchant_main as dept on dept.mc_id = m.mc_id and dept.path like '"+path+"%'";
        int terminalTotal = jdbcTemplate.queryForObject(terminalCountSql,Integer.class);

        Map<String,Object> data = new HashMap<>();

        data.put("amount",CommonUtil.formatAB(summary.get("amt-sum")));
        data.put("sharePoint",CommonUtil.formatAB(summary.get("share-sum")));
        data.put("count",summary.get("count"));
        data.put("num",merchTotal+"/"+terminalTotal);
       return R.ok().put("data",data);
    }

    @RequestMapping("/transData")
    public R transData(@RequestParam Map<String,Object> params) {
        fixParas(params);
        ITransDataService service = getService(params);
        Map<String,Object> data = new HashMap<>();

        List<String> timeList = new ArrayList<>();
        List<String> amountList = new ArrayList<>();

      //  Map<String,Object> query = new HashMap<>();
        //fixParas(query);

        Date date = new Date();
        for (int i = 7 ; i >= 1; i --){
            Date currTime = DateUtils.getNextSomeDay(date, i);
            Date dateStart = DateUtils.getStartTime(currTime);
            Date dateEnd = DateUtils.getnowEndTime(currTime);


            params.put("dateStart",dateStart);
            params.put("dateEnd",dateEnd);
            Object amount = service.
                    createGroupQuery(params)
                    .collection4Amt("sum","amount")
                    .querySingleObject();


            timeList.add(DateUtils.format(currTime, DateUtils.DATE_PATTERN));
            amountList.add(CommonUtil.formatAB(amount));
        }

        data.put("transTime",timeList);
        data.put("amount",amountList);
        return R.ok().put("data",data);
    }

    Stream<LocalDate> monthOfYears(){
       return Stream
                .iterate(LocalDate.now().plus(-12,ChronoUnit.MONTHS),
                        i->i.plus(1,ChronoUnit.MONTHS))
                .limit(12);
    }


}
