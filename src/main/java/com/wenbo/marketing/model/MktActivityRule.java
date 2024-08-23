package com.wenbo.marketing.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

/**
 * @author changwenbo
 * @date 2024/8/23 16:01
 */
@TableName("mkt_activity_rule")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MktActivityRule extends BaseMktInfo {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String activityId;

    private String ruleKey;

    private String ruleName;
}
