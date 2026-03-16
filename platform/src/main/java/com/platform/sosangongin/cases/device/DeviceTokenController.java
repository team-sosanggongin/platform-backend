package com.platform.sosangongin.cases.device;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device-tokens")
@RequiredArgsConstructor
public class DeviceTokenController {
    private final RegisterDeviceTokenUseCase registerDeviceTokenUseCase;

    @PostMapping
    public ResponseEntity<Void> registerToken(@RequestBody DeviceTokenRequest request) {
        registerDeviceTokenUseCase.registerOrUpdateToken(request);
        return ResponseEntity.ok().build();
    }
}
