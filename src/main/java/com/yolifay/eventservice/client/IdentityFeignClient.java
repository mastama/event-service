package com.yolifay.eventservice.client;

import com.yolifay.eventservice.dto.client.IdentityEnvelope;
import com.yolifay.eventservice.dto.client.WargaMinimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "identityClient",
        url = "${feign.client.identity.base.url}",
        configuration = FeignConfig.class
)
public interface IdentityFeignClient {
    @GetMapping("/warga/by-nik/{nik}" )
    IdentityEnvelope<WargaMinimal> getWargaByNik(@PathVariable("nik") String nik);
}
