package com.yolifay.eventservice.dto;

import java.time.LocalDateTime;

public record EventResponse(
        String id,
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String location,
        Integer quota,
        String description
) {
}
