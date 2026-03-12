package com.platform.sosangongin.cases.auth.verification;

import com.platform.sosangongin.domains.user.*;
import com.platform.sosangongin.services.randoms.RandomCharGeneratorService;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class PhoneVerificationUsecase {

    private final UserRepository userRepository;
    private final RandomCharGeneratorService randomCharGeneratorService;
    private final TimeGeneratorService timeGeneratorService;
    private final PhoneVerificationRepository phoneVerificationRepository;

    /**
     * 1. 사용자가 최초 회원가입을 하는 경우, 회원 테이블에 입력 후, 폰 추가 인증을 수행해야 함.
     * 2. 해당 메서드가 실시되는 때는, 추가적인 전화번호 인증이 진행되는 경우임.
     *
     **/
    public PhoneVerificationResult handlePhoneVerification(PhoneVerificationRequest req) {

        log.debug("phone number verification process begin for user {}", req.getUserId());
        Optional<User> userInfo = this.userRepository.findById(UUID.fromString(req.getUserId()));

        if (userInfo.isEmpty()) {
            log.warn("this req is invalid because user {} is not in the table", req.getUserId());
            return PhoneVerificationResult.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("this req is invalid. user is not registered with the system")
                    .build();
        }

        User user = userInfo.get();
        if (user.isPhoneVerified()) {
            log.warn("this user {} has already verified its phone-number", user.getId());
            return PhoneVerificationResult.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("this user has already verified its phone-number")
                    .build();
        }

        if (req.isPhoneVerificationRequest()) {
            Optional<PhoneVerification> mostRecentVerification = this.phoneVerificationRepository.findTopByUserOrderByCreatedAtDesc(user);
            if (mostRecentVerification.isEmpty()) {
                log.warn("this request is invalid since phone verification history is not present");
                return PhoneVerificationResult.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message("phone verification history is not present")
                        .build();
            }

            PhoneVerification phoneVerification = mostRecentVerification.get();

            if (!phoneVerification.getStatus().equals(VerificationStatus.PENDING)) {
                return PhoneVerificationResult.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message("this phone verification is in illegal state")
                        .build();
            }

            if (phoneVerification.isExpired(this.timeGeneratorService.now())) {
                log.warn("this phone verification is expired!");

                phoneVerification.setStatus(VerificationStatus.EXPIRED);

                return PhoneVerificationResult.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message("this phone verification is expired")
                        .build();
            }

            if (!phoneVerification.verify(req.getCode())) {
                log.warn("this code is invalid");
                return PhoneVerificationResult.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message("this code is invalid")
                        .build();
            } else {

                phoneVerification.verified();
                user.verifyPhone();
                return PhoneVerificationResult.builder()
                        .httpStatus(HttpStatus.OK)
                        .build();
            }
        } else {
            log.debug("this user need to verify its phone-number");
            String randomNumber = this.randomCharGeneratorService.getRandomNumber(5, UUID.randomUUID().toString());
            log.debug("random number is : {}", randomNumber);
            PhoneVerification phoneVerification = PhoneVerification.builder()
                    .code(randomNumber)
                    .expiredAt(timeGeneratorService.after(5, ChronoUnit.MINUTES))
                    .status(VerificationStatus.PENDING)
                    .user(user)
                    .build();

            this.phoneVerificationRepository.save(phoneVerification);

            return PhoneVerificationResult.builder()
                    .httpStatus(HttpStatus.OK)
                    .build();
        }
    }
}
