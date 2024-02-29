package zerobase.fintech.dto.response.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class DeleteAccountResponse {
  private String status;
  private String deletedAccountNum;

  public static DeleteAccountResponse response(String accountNum){
    return DeleteAccountResponse.builder()
        .status("계좌 삭제 성공")
        .deletedAccountNum(accountNum)
        .build();
  }
}
