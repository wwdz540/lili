package io.renren.modules.crm.controller;

import io.renren.common.utils.*;
import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.service.MerchInfoService;
import io.renren.modules.crm.service.TransDataService;
import io.renren.modules.crm.service.impl.UserGetService;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserService;
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
@RequestMapping("/crm/sharePoint")
public class SharepointController {

	@Autowired
	private TransDataService transDataService;

	@Autowired
	private UserGetService userGetService;

	@Autowired
	private SysUserService sysUserService;


	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
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

		List<Long> deptList = new ArrayList<>();
		deptList.add(6l);
		deptList.add(8l);
		List<SysUserEntity> sysUserEntityList = sysUserService.findParent(deptList);
		List<Map<String,Object>> list = new ArrayList<>();
		for (SysUserEntity sysUserEntity : sysUserEntityList) {
			List<Long> userIdList = userGetService.getUserId(sysUserEntity.getUserId());
			params.put("userIdList",userIdList);
			Map<String,Object> map = transDataService.getSumData(params,sysUserEntity);
			map.put("userName",sysUserEntity.getUsername());
			map.put("userId",sysUserEntity.getUserId());

			list.add(map);
		}

		PageUtils pageUtil = new PageUtils(list, sysUserEntityList.size(), 0, 0);

		return R.ok().put("page", pageUtil);
	}
}
