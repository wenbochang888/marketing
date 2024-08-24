package com.wenbo.marketing.service.rule;

import com.wenbo.marketing.model.rule.ActivityRuleContext;

public interface BaseRuleService {
	boolean check(ActivityRuleContext context);

	String getRuleKey();
}
