package io.renren.modules.crm.controller;

import io.renren.common.utils.*;
import io.renren.modules.crm.entity.MerchInfoEntity;
import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.service.MerchInfoService;
import io.renren.modules.crm.service.TransDataService;
import io.renren.modules.crm.service.impl.UserGetService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 交易记录
 * 
 * @author zhuhui
 */
@RestController
@RequestMapping("/crm/transData")
public class TransDataController{

	@Autowired
	private TransDataService transDataService;

	@Autowired
	private UserGetService userGetService;


	@Autowired
	private MerchInfoService merchInfoService;

	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		List<Long> userIdList= new ArrayList<>();
		//代理商
		if (CommonUtil.isDL()){
			userIdList = userGetService.getUserId(CommonUtil.getUser().getUserId());
			params.put("userIdList",userIdList);

			Long leaderId = userGetService.getUser(String.valueOf(params.get("leaderName")));
			params.put("userId",leaderId);
		}
		//商户
		if (CommonUtil.isMerch()){
			params.put("merchno",CommonUtil.getUser().getUsername());
		}

		DateUtils.fixQueryDate(params);
	//	fixQueryDate(params);
/*		//默认时间
		Object start = params.get("dateStart");
		Object end = params.get("dateEnd");
		if (start != null && end != null &&
				StringUtils.isNotBlank(String.valueOf(start))&& StringUtils.isNotBlank(String.valueOf(end))){
			String dateEnd = String.valueOf(params.get("dateEnd"))+" 23:59:59";
			params.put("dateEnd",dateEnd);
		}else{  //如果为空的话，让时间变为昨日的

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR,12);
			calendar.set(Calendar.MINUTE,00);
			calendar.set(Calendar.MINUTE,00);

			params.put("dateEnd",calendar.getTime());

			calendar.add(Calendar.DATE,-1);


			params.put("dateStart",calendar.getTime());

		//	params.put("dateStart", DateUtils.getStartTime());
		//	params.put("dateEnd", DateUtils.getnowEndTime());
		}*/

		Query query = new Query(params);
		List<TransDataEntity> list = transDataService.queryList(query);
		int total = transDataService.queryTotal(query);

		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());

		int avg = transDataService.queryAvg(query);

		BigDecimal amt = new BigDecimal(avg).divide(BigDecimal.valueOf(100),2,BigDecimal.ROUND_HALF_UP);

		return R.ok().put("page", pageUtil).put("avg",amt.toPlainString());
	}


	@RequestMapping("/excel")
	public void exportExcel(@RequestParam Map<String, Object> params, HttpServletResponse response) throws IOException {


		//默认时间
		DateUtils.fixQueryDate(params);

		String utf = "UTF-8";
		response.setContentType("application/ms-txt.numberformat:@");
		response.setCharacterEncoding(utf);
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "max-age=30");

		response.setHeader("Content-disposition", "attachment; filename=" + new String(params.get("dateStart").toString().getBytes(), "ISO8859-1")+".csv");
		PrintWriter write = response.getWriter();
		List<TransDataEntity> list = transDataService.queryList(params);

		printCsvHeader(write,new String[]{"订单号","终端流水","金额","分润",
				"商户号","商户名称","交易时间","交易参考","终端号","卡号末四位",
		"交易状态","发卡机","卡类型"});


		for (TransDataEntity e : list) {
				printCsvColumn(write,e);
		}

		write.flush();
		response.flushBuffer();
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

		//	String s = new String(columns[i].getBytes("UTF-8"),"gbk");
			writer.write(columns[i]);

		}
		writer.write("\n");

	}

	/**
	 * 交易记录   分润数据  红色字体展示
	 * @param params
	 * @return
	 */
	@RequestMapping("/sharePoint")
	public R sharePoint(@RequestParam Map<String, Object> params){
		if (CommonUtil.isMerch()){
			return R.ok();
		}
		if (CommonUtil.isDL()){
			List<Long> userIdList = userGetService.getUserId(CommonUtil.getUser().getUserId());
			params.put("userIdList",userIdList);
		}
//		//默认时间
//		String start = (String)params.get("dateStart");
//		String end =  (String)params.get("dateEnd");
//		if (start != null && end != null &&
//				StringUtils.isNotBlank(String.valueOf(start))&& StringUtils.isNotBlank(String.valueOf(end))){
//			String dateEnd = String.valueOf(params.get("dateEnd"))+" 23:59:59";
//			params.put("dateEnd",dateEnd);
//		}else{
//			params.put("dateStart", DateUtils.getStartTime());
//			params.put("dateEnd", DateUtils.getnowEndTime());
//		}

		Map<String,Object> map = transDataService.getSumData(params,null);
		return R.ok(map);
	}

	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") int id){
		TransDataEntity data = transDataService.findOne(id);
		return R.ok().put("data", data);
	}

	@RequestMapping("/transData")
	public R transData(){
		if (CommonUtil.isMerch()){
			return R.ok();
		}
		Map<String,Object> map = transDataService.transData();
		return R.ok().put("data", map);
	}

	@RequestMapping("/countTransData")
	public R countTransData(){
		if (CommonUtil.isMerch()){
			return R.ok();
		}
		Map<String,Object> map = transDataService.countTransData();
		return R.ok().put("data",map);
	}
/*

	private void fixQueryDate(Map params){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		String todayString = df.format(calendar.getTime());

		calendar.add(Calendar.DATE,-1);
		String yestodayString = df.format(calendar.getTime());


		Object start = params.get("dateStart");
		Object end = params.get("dateEnd");

		if(start == null ||StringUtils.isBlank(start.toString())){
			params.put("dateStart",yestodayString+" 12:00:00");
			params.put("dateEnd",todayString+" 12:00:00");
		}else if (StringUtils.trim(end.toString()).equals(todayString)) { //如果是今天的话
			params.put("dateStart",start+" 12:00:00");
			DateFormat sf = new SimpleDateFormat("HH:mm:ss");
			params.put("dateEnd",end+" "+ sf.format(new Date()));
		}else{
			params.put("dateStart",start+" 12:00:00");
			params.put("dateEnd",end+" 12:00:00");
		}

		System.out.println("============");
		System.out.println("start = "+params.get("dateStart"));
		System.out.println("end ="+ params.get("dateEnd"));
		System.out.println("===============");
	}
*/


}
