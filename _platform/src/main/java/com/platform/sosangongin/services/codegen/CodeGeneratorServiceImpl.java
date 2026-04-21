package com.platform.sosangongin.services.codegen;

import com.platform.sosangongin.services.randoms.RandomCharGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodeGeneratorServiceImpl implements CodeGeneratorService{
    private final RandomCharGeneratorService randomCharGeneratorService;

    @Override
    public CodeVo generateCode() {
        String code = randomCharGeneratorService.getRandomChar(6, null);
        return new CodeVo(code, "v1");
    }
}
