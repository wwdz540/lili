package io.renren.modules.crm.controller;

import io.renren.common.utils.CommonUtil;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.crm.entity.TerminalEntity;
import io.renren.modules.crm.service.TerminalService;
import io.renren.modules.crm.service.impl.UserGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 终端管理
 * 
 * @author zhuhui
 */
@RestController
@RequestMapping("/crm/terminal")
public class TerminalController{

	@Autowired
	private TerminalService terminalService;
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
		List<TerminalEntity> list = terminalService.queryList(query);
		int total = terminalService.queryTotal(query);

		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());

		return R.ok().put("page", pageUtil);
	}


	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") int id){
		TerminalEntity data = terminalService.findOne(id);
		return R.ok().put("data", data);
	}


	@RequestMapping("/save")
	public R save(@RequestBody TerminalEntity terminalEntity){
		ValidatorUtils.validateEntity(terminalEntity);
		terminalService.save(terminalEntity);
		return R.ok();
	}


	@RequestMapping("/update")
	public R update(@RequestBody TerminalEntity terminalEntity){
		ValidatorUtils.validateEntity(terminalEntity);
		terminalService.update(terminalEntity);
		return R.ok();
	}

	@RequestMapping("/delete")
	public R delete(@RequestBody Integer[] merchIds){
		terminalService.deleteBatch(merchIds);
		return R.ok();
	}
}
