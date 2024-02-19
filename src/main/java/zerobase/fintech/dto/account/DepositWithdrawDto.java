package zerobase.fintech.dto.account;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DepositWithdrawDto {
  @Positive(message = "입출금 금액은 0보다 커야합니다.")
  private int amount;

  @Size(min = 4, max = 4, message = "계좌 비밀번호는 숫자 4자리여야 합니다.")
  private String accountPassword;
}
