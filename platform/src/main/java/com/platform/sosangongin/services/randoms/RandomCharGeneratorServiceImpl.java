package com.platform.sosangongin.services.randoms;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomCharGeneratorServiceImpl implements RandomCharGeneratorService {

    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMERIC_CHARS = "0123456789";

    @Override
    public String getRandomChar(int digit, Object seed) {
        return generate(digit, seed, ALPHANUMERIC_CHARS);
    }

    @Override
    public String getRandomNumber(int digit, Object seed) {
        return generate(digit, seed, NUMERIC_CHARS);
    }

    private String generate(int length, Object seed, String charSet) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        long seedValue;
        if (seed == null) {
            // seed가 null이면 현재 시간을 사용하여 매번 다른 값이 나오도록 함 (혹은 예외 처리)
            // 여기서는 안전하게 랜덤성을 보장하기 위해 nanoTime 사용
            seedValue = System.nanoTime();
        } else {
            // 객체의 hashCode를 사용하여 시드값 생성. 
            // String, Long, Integer 등 불변 객체의 hashCode는 일정하므로 결정적임.
            seedValue = seed.hashCode();
        }

        Random random = new Random(seedValue);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charSet.length());
            sb.append(charSet.charAt(index));
        }
        return sb.toString();
    }
}
