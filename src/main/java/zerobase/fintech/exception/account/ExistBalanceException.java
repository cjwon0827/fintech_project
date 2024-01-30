package zerobase.fintech.exception.account;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class ExistBalanceException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "해당 계좌에 잔액이 존재하여 계좌를 삭제할 수 없습니다.";
  }
}
