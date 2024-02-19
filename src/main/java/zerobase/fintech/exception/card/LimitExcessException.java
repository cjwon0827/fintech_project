package zerobase.fintech.exception.card;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class LimitExcessException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "한도 초과 입니다.";
  }
}
