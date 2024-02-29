package zerobase.fintech.dto.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UpdateUserResponse {
  private String status;
  private String userName;
  private String phone;
  public static UpdateUserResponse response(String userName, String phone){
    return UpdateUserResponse.builder()
        .status("회원 정보 수정 성공")
        .userName(userName)
        .phone(phone)
        .build();
  }
}
