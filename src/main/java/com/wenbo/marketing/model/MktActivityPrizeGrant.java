package com.wenbo.marketing.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author changwenbo
 * @date 2024/8/23 16:10
 */
@TableName("mkt_activity_prize_grant")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MktActivityPrizeGrant extends BaseMktInfo {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String activityId;

    private String prizeId;

    private String grantId;


    private LocalDateTime grantTime;
}
