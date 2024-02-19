package zerobase.fintech.dto.card;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DeleteCardDto {
  @NotBlank(message = "카드 번호는 공백일 수 없습니다.")
  private String cardNum;

  @Size(min = 4, max = 4, message = "카드 비밀 번호는 숫자 4자리여야 합니다.")
  private String cardPassword;

  @Size(min = 4, max = 4, message = "계좌 비밀번호는 숫자 4자리여야 합니다.")
  private String accountPassword;
}
