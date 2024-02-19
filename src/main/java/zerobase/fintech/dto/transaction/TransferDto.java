package zerobase.fintech.dto.transaction;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TransferDto {
  @NotBlank(message = "본인 계좌번호는 공백일 수 없습니다.")
  private String startAccountNum;

  @NotBlank(message = "송금 계좌번호는 공백일 수 없습니다.")
  private String destinationAccountNum;

  @Positive(message = "송금 금액은 0보다 커야 합니다.")
  private int amount;

  @Size(min = 4, max = 4, message = "계좌 비밀번호는 숫자 4자리여야 합니다.")
  private String accountPassword;
}
