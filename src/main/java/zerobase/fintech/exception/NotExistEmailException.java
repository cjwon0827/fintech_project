package zerobase.fintech.exception;

import org.springframework.http.HttpStatus;

public class NotExistEmailException extends AbstractException{

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "존재하지 않는 이메일 입니다.";
  }
}
