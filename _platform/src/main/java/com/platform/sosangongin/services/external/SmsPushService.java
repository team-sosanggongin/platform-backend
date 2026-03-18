package com.platform.sosangongin.services.external;

public interface SmsPushService {
    /**
     * @apiNote SMS 푸시 기능을 담당하는 메서드로, 동기, 비동기식으로 처리하는 것도 고려가능
     * @implNote 반드시 메시지 성공을 가능케 해야한다. 만약, 실패할 경우, 재시도를 수행한다.
     * TODO :: 메시지 전송 기록을 남겨, 메시지가 반드시 도달하도록 보장해야 한다.
     * **/
    void send(String target, String message);
}
