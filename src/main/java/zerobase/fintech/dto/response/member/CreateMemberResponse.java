package zerobase.fintech.dto.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CreateMemberResponse {
  private String message;

  public static CreateMemberResponse response(){
    return CreateMemberResponse.builder()
        .message("이메일 인증을 통해 회원가입을 완료하세요.")
        .build();
  }
}
