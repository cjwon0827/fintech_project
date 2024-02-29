package zerobase.fintech.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PasswordCheckResponse {
  private String status;

  public static PasswordCheckResponse response(){
    return PasswordCheckResponse.builder()
        .status("비밀번호 확인 완료")
        .build();
  }
}
