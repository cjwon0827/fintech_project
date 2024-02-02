package zerobase.fintech.exception.account;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class InsufficientBalanceException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "잔액이 부족합니다.";
  }
}
