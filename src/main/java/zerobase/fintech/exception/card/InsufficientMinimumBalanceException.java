package zerobase.fintech.exception.card;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class InsufficientMinimumBalanceException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "카드 개설은 계좌 잔액 50만원 이상 부터 가능 합니다.";
  }
}
