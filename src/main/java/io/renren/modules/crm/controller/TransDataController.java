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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
		//默认时间
		Object start = params.get("dateStart");
		Object end = params.get("dateEnd");
		if (start != null && end != null &&
				StringUtils.isNotBlank(String.valueOf(start))&& StringUtils.isNotBlank(String.valueOf(end))){
			String dateEnd = String.valueOf(params.get("dateEnd"))+" 23:59:59";
			params.put("dateEnd",dateEnd);
		}else{
			params.put("dateStart", DateUtils.getStartTime());
			params.put("dateEnd", DateUtils.getnowEndTime());
		}

		Query query = new Query(params);
		List<TransDataEntity> list = transDataService.queryList(query);
		int total = transDataService.queryTotal(query);

		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());

		return R.ok().put("page", pageUtil);
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
		//默认时间
		String start = (String)params.get("dateStart");
		String end =  (String)params.get("dateEnd");
		if (start != null && end != null &&
				StringUtils.isNotBlank(String.valueOf(start))&& StringUtils.isNotBlank(String.valueOf(end))){
			String dateEnd = String.valueOf(params.get("dateEnd"))+" 23:59:59";
			params.put("dateEnd",dateEnd);
		}else{
			params.put("dateStart", DateUtils.getStartTime());
			params.put("dateEnd", DateUtils.getnowEndTime());
		}

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
}
