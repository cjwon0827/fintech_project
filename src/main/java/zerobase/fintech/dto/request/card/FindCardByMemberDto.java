package zerobase.fintech.dto.request.card;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FindCardByMemberDto {
  @Email
  @NotBlank(message = "이메일은 공백일 수 없습니다.")
  private String email;

  @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
  private String memberPassword;
}
