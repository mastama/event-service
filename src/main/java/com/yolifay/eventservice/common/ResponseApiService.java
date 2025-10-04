package com.yolifay.eventservice.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseApiService<T> {
    private String responseCode; // HTTP Status + Service ID + Case Code (eg. 40400001A01)
    private String responseDesc; // ACCOUNT NOT FOUND
    private T data;              // Data Object | payload
}
