package com.wenbo.marketing.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityRuleKeyEnum {

	CHECK_PHONE(1, "check_phone", "手机号校验"),


	;

	private Integer code;
	private String ruleKey;
	private String ruleDesc;

}
