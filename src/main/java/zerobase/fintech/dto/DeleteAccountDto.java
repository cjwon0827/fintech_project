package zerobase.fintech.dto;

import lombok.Data;

@Data
public class DeleteAccountDto {
  private String accountNum;
  private String memberPassword;
  private String accountPassword;
}
