package com.zeki.kisvolcano.domain._common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "00. Healthcheck", description = "헬스 체크를 담당한다.")
public class HealthCheckController {

    @GetMapping("/")
    @Operation(summary = "Health check")
    public String healthCheck() {
        return "";
    }
}
