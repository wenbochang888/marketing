package com.wenbo.marketing.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author changwenbo
 * @date 2024/8/23 15:57
 */
@TableName("mkt_activity_info")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MktActivityInfo extends BaseMktInfo {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String activityId;

    private String activityName;

    private LocalDateTime validityDateStart;

    private LocalDateTime validityDateEnd;

    private Integer enable;
}
