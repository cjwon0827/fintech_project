package zerobase.fintech.dto.response.account;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CreateAccountResponse {
  private String status;
  private String accountNum;
  private String createdAt;

  public static CreateAccountResponse response(String accountNum, LocalDateTime createdAt){
    return CreateAccountResponse.builder()
        .status("계좌 생성 성공")
        .accountNum(accountNum)
        .createdAt(createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        .build();
  }
}
