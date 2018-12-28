package io.renren.modules.crm.controller;

import io.renren.common.utils.*;
import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.service.ITransDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/crm/td")
public class MyTransDataController extends BaseController {

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        ITransDataService service = getService(params);
        fixParas(params);
        Query query = new Query(params);
        List<TransDataEntity> list = service.queryList(query);
        int total = service.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());

        Object avgData = service.createGroupQuery(params)
                .collection4Amt("avg","avg")
                .querySingleObject();
        
        return R.ok()
                .put("page", pageUtil)
                .put("avg",CommonUtil.formatAB(avgData));
    }

    @RequestMapping("/excel")
    public void exportExcel(@RequestParam Map<String, Object> params, HttpServletResponse response) throws IOException {
        fixParas(params);
        ITransDataService service = getService(params);
        //默认时间
        DateUtils.fixQueryDate(params);

        String utf = "UTF-8";
        response.setContentType("application/ms-txt.numberformat:@");
        response.setCharacterEncoding(utf);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");

        response.setHeader("Content-disposition", "attachment; filename=" + new String(params.get("dateStart").toString().getBytes(), "ISO8859-1")+".csv");
        PrintWriter write = response.getWriter();
        List<TransDataEntity> list = service.queryList(params);

        printCsvHeader(write,new String[]{"订单号","终端流水","金额","分润",
                "商户号","商户名称","交易时间","交易参考","终端号","卡号末四位",
                "交易状态","发卡机","卡类型"});


        for (TransDataEntity e : list) {
            printCsvColumn(write,e);
        }

        write.flush();
        response.flushBuffer();
        
        
    }

    @RequestMapping("/summary")
    public R sharePoint(@RequestParam Map<String, Object> params){
//        if (CommonUtil.isMerch()){  //@TODO 要修改
//            return R.ok();
//        }
        fixParas(params);
        DateUtils.fixQueryDate(params);
        ITransDataService service = getService(params);
        Map<String,Object> map = service
                .createGroupQuery(params)
                .collection4Share("sum","share-sum")
                .collection4Amt("avg","share-avg")
                .collection("count")
                .collection4Amt("sum","amt-sum")
                .collection4Amt("avg","amt-avg")
                .querySingleMap();

        return R.ok(map)            //@TODO 应该要返回更多内容
                .put("sharePoint",CommonUtil.formatAB(map.get("share-sum")))
                .put("amount",CommonUtil.formatAB(map.get("amt-sum")));

    }

    DateFormat df = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss");

    private void printCsvColumn(PrintWriter writer, TransDataEntity e) {

        writer.write(e.getOrderId());
        writer.write(",");
        writer.write(e.getTraceNo());
        writer.write(",");
        writer.write(e.getAmt());
        writer.write(",");
        writer.write(e.getSharePoint()+"");
        writer.write(",");
        writer.write(e.getMerchantId());
        writer.write(",");
        writer.write(e.getMerchName());
        writer.write(","); 
        writer.write(df.format(e.getTxnDatetime()));
        writer.write(",");
        writer.write(e.getTxnRef());
        writer.write(",");
        writer.write(e.getTerminalId());
        writer.write(",");
        writer.write(e.getShortPan());
        writer.write(",");
        writer.write(e.getRespCode());
        writer.write(",");
        writer.write(e.getIssuerCode());
        writer.write(",");
        writer.write(e.getCardType());

        writer.write("\n");
    }

    private void printCsvHeader(PrintWriter writer,String[] columns) throws UnsupportedEncodingException {
        writer.write(columns[0]);
        for(int i=1;i<columns.length;i++){
            writer.write(",");
            writer.write(columns[i]);
        }
        writer.write("\n");

    }



    @Autowired
    private Map<String,ITransDataService>  serviceMap;
    public ITransDataService getService(Map<String,Object> params){
        return serviceMap.get(params.get("ds")); //获取数据源，富友或盛富通的
    }

}
