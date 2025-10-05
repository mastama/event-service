package com.yolifay.eventservice.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Constants {

    @Getter
    public enum RESPONSE {
        APPROVED("00", "Approved"),
        CREATED("01", "Created"),

        BAD_REQUEST("40", "Permintaan tidak valid"),
        UNAUTHORIZED("41", "Unauthorized"),
        FORBIDDEN("43", "Forbidden"),
        HTTP_NOT_FOUND("44", "There is No Resource Path"),
        METHOD_NOT_ALLOWED("45", "Method Not Allowed"),
        UNSUPPORTED_MEDIA_TYPE("47", "Unsupported Media Type"),
        INVALID_INPUT("48", "Invalid Input"),
        BAD_GATEWAY("52", "Bad Gateway"),
        SERVICE_UNAVAILABLE("53", "Service Unavailable"),
        GATEWAY_TIMEOUT("54", "Gateway Timeout"),

        DATA_EXISTS("15", "Data sudah ada"),
        ACCOUNT_NOT_FOUND("14", "Data tidak ditemukan"),
        INVALID_CREDENTIALS("51", "Username/Password salah"),
        TRANSACTION_TIMEOUT("68", "Transaction Timeout"),
        HTTP_INTERNAL_ERROR("X5", "Service Internal Error"),
        TOO_MANY_REQUESTS("99", "Too Many Requests");

        private final String code;
        private final String description;

        RESPONSE(String code, String description) {
            this.code = code;
            this.description = description;
        }

    }
}