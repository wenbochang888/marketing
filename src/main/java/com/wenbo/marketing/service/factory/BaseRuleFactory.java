package com.wenbo.marketing.service.factory;

import com.wenbo.marketing.service.rule.BaseRuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class BaseRuleFactory {
	private static final Map<String, BaseRuleService> BASE_RULE_MAP = new HashMap<>(4);

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void init() {
		Map<String, BaseRuleService> baseRuleServiceMap = applicationContext.getBeansOfType(BaseRuleService.class);
		if (MapUtils.isEmpty(baseRuleServiceMap)) {
			return;
		}

		for (Map.Entry<String, BaseRuleService> entry : baseRuleServiceMap.entrySet()) {
			BaseRuleService baseRuleService = entry.getValue();
			BASE_RULE_MAP.put(baseRuleService.getRuleKey(), baseRuleService);
		}

		log.info("BaseRuleFactory init success, map = {}", BASE_RULE_MAP);
	}

	public static BaseRuleService getBaseRuleService(String ruleKey) {
		return BASE_RULE_MAP.get(ruleKey);
	}
}
