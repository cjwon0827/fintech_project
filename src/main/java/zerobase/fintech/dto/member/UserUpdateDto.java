package zerobase.fintech.dto.member;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserUpdateDto {
  @NotBlank(message = "이름은 공백일 수 없습니다.")
  private String userName;

  @NotBlank(message = "전화번호는 공백일 수 없습니다.")
  private String phone;
}
