package zerobase.fintech.exception;

import org.springframework.http.HttpStatus;

public class MemberNotEmailAuthException extends AbstractException {

  @Override
  public int getStatusCode(){
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage(){
    return "이메일 인증 완료 후 로그인 가능합니다.";
  }
}
