# 핀테크 프로젝트
사용자는 회원가입 후 계좌를 생성하여 입금, 송금, 및 신용카드 생성 기능을 이용할 수 있는 핀테크 프로젝트 입니다.

## 사용 기술 스택
* JAVA
* SpringBoot
* Spring Security
* Maria DB
* JPA
* GIT

## 프로젝트 구조
![스크린샷 2024-01-17 192809](https://github.com/cjwon0827/fintech_project/assets/83802761/b065025c-fdb8-4c8b-9be0-8a0ae65bdc66)



## ERD
![스크린샷 2024-02-19 201657](https://github.com/cjwon0827/fintech_project/assets/83802761/1f9bdaef-faf4-48aa-9b5d-51e73976badb)




## 프로젝트 기능 및 설계
* 회원
  * 회원 가입
     * 사용자는 회원가입을 할 수 있다. 모든 사용자는 회원가입시 USER 권한 (일반 권한)을 지닌다.
     * 회원 가입 시 이메일(아이디), 비밀번호, 이름, 전화번호를 입력 하며, 이메일은 unique 해야 하고 이메일 형식에 맞게 입력해야 한다.
     * 회원 가입 정보 입력 완료 후 입력 한 이메일로 인증 메일이 발송 되고, 해당 이메일에서 링크를 클릭하면 회원 가입이 완료된다.
       
  * 회원 정보 수정
     * 회원은 이름, 전화번호 정보를 수정할 수 있다.
   
  * 회원 탈퇴
     * 회원은 계좌가 존재하지 않을 때 회원 탈퇴를 진행할 수 있다.
      
* 로그인
  * 사용자는 로그인을 할 수 있다. 로그인 시 회원가입 때 입력한 이메일(아이디)과 패스워드가 일치해야한다.

 
* 계좌
  * 계좌 생성
    * 회원은 계좌를 생성할 수 있다.
    * 한 회원 당 최대 10개 까지 계좌를 생성할 수 있다.

  * 계좌 입금
    * 회원은 본인 계좌에 입금할 수 있다.
    * 입금이 완료되면 거래 내역에 저장
   
  * 계좌 출금
    * 회원은 본인 계좌에서 출금할 수 있다. 계좌 잔액보다 많은 금액을 출금하려고 할 시 예외 발생
    * 출금이 완료되면 거래 내역에 저장
    
  * 계좌 송금
    * 회원은 다른 계좌에 현재 잔액까지만 송금할 수 있다. 송금하려는 계좌가 존재하지 않거나, 본인의 계좌에 존재하는 금액보다 큰 금액을 송금하려는 경우 예외 발생
    * 송금이 완료되면 거래 내역에 저장

  * 계좌 해지(삭제)
    * 회원은 계좌 잔액이 0원이고, 해당 계좌로 연동된 카드가 존재하지 않을 경우 해당 계좌를 해지(삭제)할 수 있다.
   
  * 계좌 검색 및 조회
    * 회원은 본인의 계좌를 검색 및 조회할 수 있다.


* 신용카드
  * 카드 생성
    * 회원은 50만원 이상의 본인 계좌를 연동하여 카드를 생성할 수 있다.
    * 카드 생성에 필요한 값은 연동 계좌번호, 계좌 비밀번호, 카드 비밀번호, 한도, 매월 납부일(1~31)이다.
    * 회원은 한 계정 당 하나의 카드만 생성할 수 있다.

  * 카드 사용
    * 회원은 본인이 설정 한 한도 금액까지 카드를 사용할 수 있다. 한도 초과가 된 경우 예외 발생
    * 카드 사용이 정지 된 카드는 사용할 수 없다.

  * 카드 리스트 조회
    * 회원은 계정 별, 연동 계좌 별로 카드 내역(카드번호, 사용 금액, 한도 등)을 조회할 수 있다.
   
  * 카드 사용 내역 조회
    * 회원은 본인이 설정한 기간 만큼 카드 사용 내역을 조회할 수 있다.

  * 카드 납부
    * 회원은 본인이 설정 한 납부일 오전 9시에 납부가 진행되거나 즉시 납부를 통해 납부시킬 수 있다.
    * 만약 계좌 잔액보다 카드 사용량이 많다면 계좌 잔액을 모두 인출한 뒤 카드를 정지 상태로 만든다.
    * 카드 정지자가 정지를 해제하고 싶은 경우 연동 계좌에 금액을 입금한 뒤 연체된 사용료 만큼 납부 시 정지가 해제된다.
    * 카드 납부가 정상적으로 진행 된 경우 거래 내역에 저장

  * 카드 해지(삭제)
    * 회원은 카드 납부가 완료된 상태이고, 카드가 정지된 상태가 아니라면 카드를 해지할 수 있다.

   
* 거래
  * 거래 내역 확인
    * 회원은 본인의 거래 내역 타입(입금, 출금, 송금, 카드 사용, 카드 납부)별로 거래 내역을 조회할 수 있다.

## 주차별 개발 계획
* 2주차 : 회원가입, 로그인, 회원 CRUD 구현
* 3주차 : 계좌 CRUD 구현
* 4주차 : 계좌 입금 및 송금 기능 구현 및 예외 처리
* 5주차 : 신용카드 생성, 사용, 납부, 삭제 기능 구현 및 거래 내역 작성
