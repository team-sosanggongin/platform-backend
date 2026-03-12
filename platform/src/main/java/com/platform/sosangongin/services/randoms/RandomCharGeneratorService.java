package com.platform.sosangongin.services.randoms;

public interface RandomCharGeneratorService {
    String getRandomChar(int digit, Object seed);
    String getRandomNumber(int digit, Object seed);
}
