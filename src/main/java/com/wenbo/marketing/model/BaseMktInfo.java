package com.wenbo.marketing.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * @author changwenbo
 * @date 2024/8/23 16:02
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseMktInfo {
    private LocalDateTime updatedAt;

    private LocalDateTime createdAt;
}
