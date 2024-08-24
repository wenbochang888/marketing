package com.wenbo.marketing.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnableEnum {

	DISABLE(0, "无效"),
	ENABLE(1, "有效"),

	;

	private Integer code;
	private String desc;

}
