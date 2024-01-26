package zerobase.fintech.exception;

import org.springframework.http.HttpStatus;

public class CheckPasswordException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "비밀번호 확인 후 다시 입력하세요.";
  }
}
