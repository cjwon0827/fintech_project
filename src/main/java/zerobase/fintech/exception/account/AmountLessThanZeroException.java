package zerobase.fintech.exception.account;

import org.springframework.http.HttpStatus;
import zerobase.fintech.exception.AbstractException;

public class AmountLessThanZeroException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "입금 금액은 0원 보다 커야합니다.";
  }
}
