package com.yolifay.eventservice.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WargaMinimal(
    String id,
    String nama,
    String nik,
    String noHp
) {
}
