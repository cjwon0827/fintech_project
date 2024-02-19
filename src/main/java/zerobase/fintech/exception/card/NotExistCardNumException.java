package zerobase.fintech.exception.card;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class NotExistCardNumException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "해당 카드 번호가 존재 하지 않습니다.";
  }
}
