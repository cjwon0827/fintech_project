package zerobase.fintech.exception.member;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class NotSamePasswordException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "비밀번호가 일치하지 않습니다.";
  }
}
