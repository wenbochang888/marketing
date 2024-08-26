package com.wenbo.marketing.controller;

import com.wenbo.marketing.model.*;
import com.wenbo.marketing.service.MktActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Controller
@RequestMapping("/marketing")
public class MarketingActivityController {

	@Autowired
	private MktActivityService mktActivityService;

	@GetMapping("/rule/check")
	public String checkActivityPage() {
		return "checkActivity";
	}

	@PostMapping("/rule/check")
	@ResponseBody
	public MktActivityInfo checkActivity(String phone) {
		return mktActivityService.checkActivityRule(phone);
	}

	@RequestMapping("/activity/grant")
	@ResponseBody
	public CommonResult grantActivityPrize(String phone, String activityId) {
		log.info("grantActivityPrize phone = {}, activityId = {}", phone, activityId);
		boolean success = mktActivityService.grantPrize(phone, activityId);
		if (success) {
			return CommonResult.success();
		}

		return CommonResult.fail();
	}

	@RequestMapping("/activity/list/grant")
	@ResponseBody
	public List<MktActivityPrizeGrant> listGrant(String phone) {
		return mktActivityService.listAllMktActivityPrizeGrant();
	}
}






















