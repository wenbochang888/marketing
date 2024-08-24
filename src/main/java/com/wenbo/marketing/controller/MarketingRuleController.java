package com.wenbo.marketing.controller;

import com.wenbo.marketing.model.MktActivityInfo;
import com.wenbo.marketing.service.MktActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
@RequestMapping("/marketing/rule")
public class MarketingRuleController {

	@Autowired
	private MktActivityService mktActivityService;

	@RequestMapping("/check")
	@ResponseBody
	public MktActivityInfo checkActivity(String phone) {
		return mktActivityService.checkActivityRule(phone);
	}
}






















