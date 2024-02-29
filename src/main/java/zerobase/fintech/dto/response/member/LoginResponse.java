package zerobase.fintech.dto.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LoginResponse {
  private String status;
  private String token;

  public static LoginResponse response(String token){
    return LoginResponse.builder()
        .status("로그인 성공")
        .token(token)
        .build();
  }
}
