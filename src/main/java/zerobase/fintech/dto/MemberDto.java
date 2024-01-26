package zerobase.fintech.dto;

import lombok.Data;

@Data
public class MemberDto {
  private String email;       //이메일(아이디)
  private String password;    //비밀번호
  private String userName;    //이름
  private String phone;       //전화번호
}
