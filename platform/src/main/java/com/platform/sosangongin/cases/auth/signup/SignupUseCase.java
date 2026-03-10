package com.platform.sosangongin.cases.auth.signup;

public class SignupUseCase {

     /**
      * 사용자가 최초 소셜 로그인을 통해 회원가입한 경우를 나타낸다.
      * 먼저 소셜 로그인 PK를 이용해 로그인한 경우가 있는지 확인하고, 없을 경우
      * 인증 작업을 수행 및, 정보를 수집한다.
      *
      * 이후, User 테이블에 User 정보를 넣고, 전화번호 인증 필요 상태로 변경한다
      *
     **/

     public SignupResult signup(SignupRequest req){

        return null;

     }



}
