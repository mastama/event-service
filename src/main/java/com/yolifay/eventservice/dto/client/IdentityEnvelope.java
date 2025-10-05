package com.yolifay.eventservice.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IdentityEnvelope<T>(
        String responseCode,
        String responseDesc,
        T data
) {}
