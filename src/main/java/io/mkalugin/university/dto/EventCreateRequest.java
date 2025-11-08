package io.mkalugin.university.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для запроса для создания события.
 */
@Data
public class EventCreateRequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String annotation;
    private String notes;
    private LocalDateTime reminderTime;
    private Long userId;
}
