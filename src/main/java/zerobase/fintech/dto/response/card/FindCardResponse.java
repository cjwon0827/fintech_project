package zerobase.fintech.dto.response.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import zerobase.fintech.entity.CreditCard;

@Builder
@Getter
@AllArgsConstructor
public class FindCardResponse {
  private String cardNum;
  private int usageAmount;
  private int availableAmount;
  private int limitAmount;

  public static FindCardResponse response(CreditCard creditCard){
    return FindCardResponse.builder()
        .cardNum(creditCard.getCardNum())
        .usageAmount(creditCard.getUsageAmount())
        .availableAmount(creditCard.getLimitAmount() - creditCard.getUsageAmount())
        .limitAmount(creditCard.getLimitAmount())
        .build();
  }
}
