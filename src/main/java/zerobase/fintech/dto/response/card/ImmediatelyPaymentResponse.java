package zerobase.fintech.dto.response.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ImmediatelyPaymentResponse {
  private String status;
  private int immediatelyPayAmount;
  private int availableImmediatelyPayAmount;

  public static ImmediatelyPaymentResponse response(int immediatelyPayAmount, int availableImmediatelyPayAmount){
    return ImmediatelyPaymentResponse.builder()
        .status("결제 완료")
        .immediatelyPayAmount(immediatelyPayAmount)
        .availableImmediatelyPayAmount(availableImmediatelyPayAmount)
        .build();
  }
}
