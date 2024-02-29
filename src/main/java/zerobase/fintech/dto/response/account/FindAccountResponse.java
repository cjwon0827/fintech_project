package zerobase.fintech.dto.response.account;

import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import zerobase.fintech.entity.Account;

@Builder
@Getter
@AllArgsConstructor
public class FindAccountResponse {

  private String accountNum;
  private int balance;
  private String createdAt;
  public static FindAccountResponse response(Account account){
    return FindAccountResponse.builder()
        .accountNum(account.getAccountNum())
        .balance(account.getBalance())
        .createdAt(account.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        .build();
  }
}
