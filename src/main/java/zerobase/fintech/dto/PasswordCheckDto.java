package zerobase.fintech.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordCheckDto {
  @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
  private String password;
}
