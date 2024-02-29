package zerobase.fintech.dto.response.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import zerobase.fintech.entity.CreditCard;

@Builder
@Getter
@AllArgsConstructor
public class UsageCardResponse {
  private String status;
  private int payAmount;

  public static UsageCardResponse response(int payAmount){
    return UsageCardResponse.builder()
        .status("결제 완료")
        .payAmount(payAmount)
        .build();
  }
}
