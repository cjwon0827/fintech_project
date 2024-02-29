package zerobase.fintech.dto.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FindUserResponse {
  private String email;
  private String userName;
  private String phone;
  public static FindUserResponse response(String email, String userName, String phone){
    return FindUserResponse.builder()
        .email(email)
        .userName(userName)
        .phone(phone)
        .build();
  }
}
