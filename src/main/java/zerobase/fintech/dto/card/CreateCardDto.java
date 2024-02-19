package zerobase.fintech.dto.card;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class CreateCardDto {
  @NotBlank(message = "연동 계좌는 빈칸일 수 없습니다.")
  private String linkedAccount;

  @Size(min = 4, max = 4, message = "계좌 비밀번호는 숫자 4자리여야 합니다.")
  private String accountPassword;

  @Size(min = 4, max = 4, message = "카드 비밀번호는 숫자 4자리여야 합니다.")
  private String cardPassword;

  @Range(min = 1000, max = 10000000, message = "한도 금액은 천원 이상 천만원 이하로 설정 가능합니다.")
  private int limitAmount;

  @Range(min = 1, max = 31, message = "결제일은 1일에서 31일 까지만 설정 가능 합니다.")
  private int monthlyDay;

  @Range(min = 1, max = 5, message = "카드 유효기간은 1 ~ 5년 까지만 설정 가능 합니다.")
  private int expirationYear;
}
