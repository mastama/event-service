package com.yolifay.eventservice.client;

import com.yolifay.eventservice.exception.ConflictException;
import com.yolifay.eventservice.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityClientFacade {
    private final IdentityFeignClient identityClient;

    public boolean existsWargaByNik(String nik) {
        log.info("client exists warga by Nik: {}", nik);
        try {
            var env = identityClient.getWargaByNik(nik);
            return env != null && env.data()!= null;
        } catch (DataNotFoundException e) {
            log.warn("Warga dengan NIK {} tidak ditemukan di identity-service", nik);
            return false;
        } catch (ConflictException e) {
            log.error("Terjadi konflik saat memeriksa NIK {} di identity-service: {}", nik, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Upstream Gagal terhubung ke identity-service saat memeriksa NIK {}: {}", nik, e.getMessage());
            throw new IllegalStateException("Gagal terhubung ke identity-service", e);
        }
    }
}
