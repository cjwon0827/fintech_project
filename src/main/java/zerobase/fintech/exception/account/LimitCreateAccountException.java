package zerobase.fintech.exception.account;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class LimitCreateAccountException extends AbstractException {
  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "개인 당 최대 10개의 계좌 개설만 가능합니다.";
  }
}
