package zerobase.fintech.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zerobase.fintech.type.MemberRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "member")
public class Member {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;

  @Column(unique = true, nullable = false)
  private String email;

  private String password;

  @Column(name = "user_name")
  private String userName;

  private String phone;

  @Column(name = "registered_at")
  private LocalDateTime registeredAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "email_auth_key")
  private String emailAuthKey;

  @Column(name = "email_auth_yn")
  private boolean emailAuthYN;

  @Column(name = "update_password_check")
  private boolean updatePasswordCheck;


  @Enumerated(EnumType.STRING)
  private MemberRole role; //사용자 권한
}
