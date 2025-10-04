package com.yolifay.eventservice.dto;

import jakarta.validation.constraints.Pattern;

public record RegisterParticipantRequest(
        @Pattern(regexp="\\d{16}", message="NIK harus 16 digit") String wargaNik
) {
}
