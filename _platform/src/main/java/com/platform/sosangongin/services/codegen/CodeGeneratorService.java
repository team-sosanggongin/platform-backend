package com.platform.sosangongin.services.codegen;

public interface CodeGeneratorService {
    /**
     * @apiNote 유저의 유일식별자를 생성한다. 이 값은 반드시 존재하지 않는 임의의 값이어야 한다.
     * @return CodeVo 코드값과, 버전을 함께 반환한다. 버전이 존재하는 이유는, 코드 생성 방식이 변경된 경우, 버전 정보도 함께 변경되어야 하기 때문이다.
     * **/
    CodeVo generateCode();
}
