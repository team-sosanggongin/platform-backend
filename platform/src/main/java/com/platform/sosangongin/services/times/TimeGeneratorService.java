package com.platform.sosangongin.services.times;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

public interface TimeGeneratorService {
    LocalDateTime now();
    LocalDateTime after(long amount, TemporalUnit unit);
    LocalDateTime before(long amount, TemporalUnit unit);
}
