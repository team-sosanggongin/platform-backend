1. 사용자 회원가입 
    - 사용자는 회원가입 시, 전화번호 인증을 수행해야 함.
    - 전화번호 인증은 SMS 인증을 통해 수행되며, 수신한 코드를 이용해 인증을 수행
    - 코드는 유효기간이 있으며, 유효기한 내에 처리가 완료되어야 함
    - 전화번호 인증이 완료되지 않은 사용자는 서비스 이용이 불가능함.
2. 사용자 인증 전략
    - 최초 로그인 시, JWT 토큰 발급(userId만 포함 - accessToken, refreshToken)
    - 이후, 브랜치에 접근하고자 할 떄, 사업체 정보 및, 해당 사업체 관련 롤을 포함하는 토큰을 refreshToken을 이용해 발급
    - 이후, 해당 브랜치 내에서는 이 토큰을 이용
2. 토큰 관리
    - 토큰은 기본적으로 AccessToken, RefreshToken으로 나뉨
    - accessToken은 사용자의 식별자와 지점 정보 및 롤을 포함
    - accessToken은 유효기간이 매우 짧고(3분) refreshToken은 길게 잡음(1주일)
    - accessToken의 유효기간이 만료된 경우(403), 클라이언트는 refreshToken을 이용해 accessToken을 재발급 받아서, 재요청