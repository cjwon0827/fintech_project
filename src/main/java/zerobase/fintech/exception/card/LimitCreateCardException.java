package zerobase.fintech.exception.card;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class LimitCreateCardException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "한 계좌 당 하나의 카드 개설만 가능 합니다.";
  }
}
