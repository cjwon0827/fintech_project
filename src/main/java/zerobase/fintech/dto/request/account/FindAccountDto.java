package zerobase.fintech.dto.request.account;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class FindAccountDto {
  @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
  private String memberPassword;

}
