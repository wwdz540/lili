package io.renren.modules.crm.controller;

import io.renren.common.utils.CommonUtil;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.crm.entity.MerchInfoEntity;
import io.renren.modules.crm.service.MerchInfoService;
import io.renren.modules.crm.service.impl.UserGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 商户管理
 * 
 * @author zhuhui
 */
@RestController
@RequestMapping("/crm/merchInfo")
public class MerchInfoController{

	@Autowired
	private MerchInfoService merchInfoService;

	@Autowired
	private UserGetService userGetService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		if (!CommonUtil.isAdmin()){
			params.put("userIdList",userGetService.getUserId(CommonUtil.getUser().getUserId()));
		}
		Query query = new Query(params);
		List<MerchInfoEntity> list = merchInfoService.queryList(query);
		int total = merchInfoService.queryTotal(query);

		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());

		return R.ok().put("page", pageUtil);
	}


	@RequestMapping("/info/{merchId}")
	public R info(@PathVariable("merchId") int merchId){
		MerchInfoEntity data = merchInfoService.findOne(merchId);
		return R.ok().put("data", data);
	}


	@RequestMapping("/save")
	public R save(@RequestBody MerchInfoEntity merchInfoEntity){
		ValidatorUtils.validateEntity(merchInfoEntity);
		merchInfoService.save(merchInfoEntity);
		return R.ok();
	}


	@RequestMapping("/update")
	public R update(@RequestBody MerchInfoEntity merchInfoEntity){
		ValidatorUtils.validateEntity(merchInfoEntity);
		merchInfoService.update(merchInfoEntity);
		return R.ok();
	}

	@RequestMapping("/delete")
	public R delete(@RequestBody Integer[] merchIds){
		merchInfoService.deleteBatch(merchIds);
		return R.ok();
	}

	@RequestMapping("/getMerch")
	public R info(){
		List<MerchInfoEntity> data = merchInfoService.findAll();
		return R.ok().put("data", data);
	}
}
