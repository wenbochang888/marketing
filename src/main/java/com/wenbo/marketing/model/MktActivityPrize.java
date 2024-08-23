package com.wenbo.marketing.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author changwenbo
 * @date 2024/8/23 16:05
 */
@TableName("mkt_activity_prize")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MktActivityPrize extends BaseMktInfo {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String activityId;

    private String prizeId;

    private String prizeName;

    private BigDecimal prizeAmount;

    private Integer prizeTotalNum;

    private Integer prizeRemainingNum;

    private Integer prizeOccupyNum;

    private Integer enable;
}
