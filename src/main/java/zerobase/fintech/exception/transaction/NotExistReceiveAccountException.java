package zerobase.fintech.exception.transaction;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class NotExistReceiveAccountException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "수신자의 계좌가 존재하지 않습니다.";
  }
}
