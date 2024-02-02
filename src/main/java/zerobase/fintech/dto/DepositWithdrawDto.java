package zerobase.fintech.dto;

import lombok.Data;

@Data
public class DepositWithdrawDto {
  private int amount;
  private String accountPassword;
}
