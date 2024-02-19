package zerobase.fintech.exception.card;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class CanNotDeleteCardException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "카드 이체 금액이 남아 있거나 정지된 카드는 해지할 수 없습니다.";
  }
}
