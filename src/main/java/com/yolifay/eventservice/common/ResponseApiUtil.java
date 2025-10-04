package com.yolifay.eventservice.common;

public final class ResponseApiUtil {

    private ResponseApiUtil() {
        throw new IllegalStateException("Utility Class");
    }

    // format code: [HTTP-3digit][SERVICE_ID][CASE_CODE]
    // contoh: 404 + 00001 + A01  => "40400001A01"
    private static String formatCode(int httpStatus, String serviceId, String caseCode) {
        String http3 = String.format("%03d", httpStatus);
        String svc   = serviceId == null ? "" : serviceId;
        String code  = caseCode == null ? "" : caseCode;
        return http3 + svc + code;
    }

    /**
     * Set response service (versi enum constant)
     */
    public static <T> ResponseApiService<T> setResponse(
            int httpStatus, String serviceId, Constants.RESPONSE response, T obj) {

        return ResponseApiService.<T>builder()
                .responseCode(formatCode(httpStatus, serviceId, response.getCode()))
                .responseDesc(response.getDescription())
                .data(obj)
                .build();
    }

    /**
     * Set response service (versi custom code + description)
     */
    public static <T> ResponseApiService<T> setResponse(
            int httpStatus, String serviceId, String caseCode, String description, T obj) {

        return ResponseApiService.<T>builder()
                .responseCode(formatCode(httpStatus, serviceId, caseCode))
                .responseDesc(description)
                .data(obj)
                .build();
    }
}