package com.yolifay.eventservice.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ConstantsProperties {

    @Value("${service.id}")
    private String serviceId;
}
