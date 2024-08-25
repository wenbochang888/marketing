package com.wenbo.marketing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult {

	private boolean success;

	private String msg;

	public static CommonResult success() {
		return new CommonResult(true, "success");
	}

	public static CommonResult success(String msg) {
		return new CommonResult(true, msg);
	}

	public static CommonResult fail() {
		return new CommonResult(false, "fail");
	}

	public static CommonResult fail(String msg) {
		return new CommonResult(false, msg);
	}
}
