package zerobase.fintech.dto.member;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberDto {
  @Email
  @NotBlank(message = "이메일은 공백일 수 없습니다.")
  private String email;       //이메일(아이디)

  @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
  private String password;    //비밀번호

  @NotBlank(message = "이름은 공백일 수 없습니다.")
  private String userName;    //이름

  @NotBlank(message = "전화번호는 공백일 수 없습니다.")
  private String phone;       //전화번호
}
