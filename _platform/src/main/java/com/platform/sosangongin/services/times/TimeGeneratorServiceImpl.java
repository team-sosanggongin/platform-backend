package com.platform.sosangongin.services.times;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

@Service
public class TimeGeneratorServiceImpl implements TimeGeneratorService {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    @Override
    public LocalDateTime after(long amount, TemporalUnit unit) {
        return LocalDateTime.now().plus(amount, unit);
    }

    @Override
    public LocalDateTime before(long amount, TemporalUnit unit) {
        return LocalDateTime.now().minus(amount, unit);
    }
}
