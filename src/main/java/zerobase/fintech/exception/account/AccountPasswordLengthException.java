package zerobase.fintech.exception.account;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class AccountPasswordLengthException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "계좌 비밀번호는 숫자 4자리만 가능합니다.";
  }
}
