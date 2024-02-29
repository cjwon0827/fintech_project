package zerobase.fintech.dto.response.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class DepositWithdrawResponse {

  private int amount;
  private int balance;

  public static DepositWithdrawResponse response(int amount, int balance){
    return DepositWithdrawResponse.builder()
        .amount(amount)
        .balance(balance)
        .build();
  }
}
