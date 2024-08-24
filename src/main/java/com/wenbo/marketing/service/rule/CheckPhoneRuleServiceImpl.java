package com.wenbo.marketing.service.rule;

import cn.hutool.core.lang.Validator;
import com.wenbo.marketing.constant.ActivityRuleKeyEnum;
import com.wenbo.marketing.model.rule.ActivityRuleContext;
import com.wenbo.marketing.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CheckPhoneRuleServiceImpl implements BaseRuleService {

	@Override
	public boolean check(ActivityRuleContext context) {
		log.info("CheckPhoneRuleServiceImpl check phone, context:{}", GsonUtil.toJson(context));
		String phone = context.getPhone();
		if (StringUtils.isEmpty(phone) || !Validator.isMobile(phone)) {
			return false;
		}

		// 暂定手机号尾号为偶数可以通过，奇数没有抽奖资格  todo-wenbo 这里随时可以进行秀修改
		int lastNum = Integer.parseInt(phone.substring(phone.length() - 1));
		if (lastNum % 2 == 0) {
			return true;
		}

		return false;
	}

	@Override
	public String getRuleKey() {
		return ActivityRuleKeyEnum.CHECK_PHONE.getRuleKey();
	}
}
