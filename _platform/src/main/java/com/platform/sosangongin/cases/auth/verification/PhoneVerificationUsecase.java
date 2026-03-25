package com.platform.sosangongin.cases.auth.verification;

import com.platform.sosangongin.domains.user.*;
import com.platform.sosangongin.domains.user.verification.PhoneVerification;
import com.platform.sosangongin.domains.user.verification.PhoneVerificationRepository;
import com.platform.sosangongin.domains.user.verification.PhoneVerificationStatus;
import com.platform.sosangongin.errors.EntityNotFoundException;
import com.platform.sosangongin.errors.EntityType;
import com.platform.sosangongin.services.randoms.RandomCharGeneratorService;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
@Transactional // 데이터 일관성을 위해 추가
public class PhoneVerificationUsecase {

    private final UserRepository userRepository;
    private final RandomCharGeneratorService randomCharGeneratorService;
    private final TimeGeneratorService timeGeneratorService;
    private final PhoneVerificationRepository phoneVerificationRepository;

    public PhoneVerificationResult handlePhoneVerification(PhoneVerificationRequest req) throws EntityNotFoundException{
        User user = userRepository.findById(UUID.fromString(req.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException(req.getUserId(), EntityType.USER, "user is not found")); // 예외 처리 예시

        if (user.isPhoneVerified()) {
            return errorResult(HttpStatus.BAD_REQUEST, "Already verified");
        }

        return req.isPhoneVerificationRequest()
                ? verifyCode(user, req.getCode())
                : sendVerificationCode(user);
    }

    private PhoneVerificationResult verifyCode(User user, String code) {
        Optional<PhoneVerification> verificationOptional = phoneVerificationRepository.findTopByUserOrderByCreatedAtDesc(user);

        if(verificationOptional.isEmpty()){
            return errorResult(HttpStatus.NOT_FOUND, "history is not present");
        }

        PhoneVerification verification = verificationOptional.get();

        // 유효성 체크 로직을 도메인 내부로 위임하는 것을 추천
        if (!verification.isVerifiable()) {
            return errorResult(HttpStatus.BAD_REQUEST, "verification state is illegal");
        }

        if (verification.isExpired(timeGeneratorService.now())) {
            verification.setStatus(PhoneVerificationStatus.EXPIRED);
            return errorResult(HttpStatus.BAD_REQUEST, "Expired code");
        }

        if (!verification.verify(code)) {
            return errorResult(HttpStatus.BAD_REQUEST, "Invalid code");
        }

        // 성공 처리
        verification.verified();
        user.verifyPhone(); // Dirty Checking으로 저장됨

        return PhoneVerificationResult.builder().httpStatus(HttpStatus.OK).build();
    }

    private PhoneVerificationResult sendVerificationCode(User user) {
        String randomNumber = randomCharGeneratorService.getRandomNumber(5, UUID.randomUUID().toString());

        PhoneVerification phoneVerification = PhoneVerification.builder()
                .code(randomNumber)
                .expiredAt(timeGeneratorService.after(5, ChronoUnit.MINUTES))
                .status(PhoneVerificationStatus.PENDING)
                .user(user)
                .build();

        phoneVerificationRepository.save(phoneVerification);
        // TODO: smsPushService.send(user.getPhoneNumber(), randomNumber);

        return PhoneVerificationResult.builder().httpStatus(HttpStatus.OK)
                .build();
    }

    private PhoneVerificationResult errorResult(HttpStatus status, String message) {
        return PhoneVerificationResult.builder().httpStatus(status).message(message).build();
    }
}