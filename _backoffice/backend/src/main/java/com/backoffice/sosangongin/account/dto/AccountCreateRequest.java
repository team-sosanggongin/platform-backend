package com.backoffice.sosangongin.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountCreateRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 50, message = "이름은 50자 이내여야 합니다.")
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Size(max = 20, message = "전화번호는 20자 이내여야 합니다.")
    private String phoneNumber;
}