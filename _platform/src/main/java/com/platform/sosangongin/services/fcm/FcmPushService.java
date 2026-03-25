package com.platform.sosangongin.services.fcm;

public interface FcmPushService {
    /**
     * FCM을 통해 단일 디바이스에 푸시 알림을 발송한다.
     *
     * @param request 발송 대상 디바이스 토큰, 제목, 본문, 추가 데이터를 포함한 요청
     * @return 발송 성공 여부, 메시지 ID, 실패 시 사유를 포함한 결과
     */
    FcmPushResult send(FcmPushRequest request);
}
