package zerobase.fintech.dto.response.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class DeleteUserResponse {
  private String status;
  private String deletedEmail;
  private String message;

  public static DeleteUserResponse okResponse(String deletedEmail){
    return DeleteUserResponse.builder()
        .status("삭제 성공")
        .deletedEmail(deletedEmail)
        .build();
  }

  public static DeleteUserResponse errorResponse(){
    return DeleteUserResponse.builder()
        .status("삭제 실패")
        .message("현재 계정에 계좌가 존재 하여 회원 탈퇴가 불가능 합니다. 계좌 해지 후 다시 시도 하십시오.")
        .build();
  }
}
