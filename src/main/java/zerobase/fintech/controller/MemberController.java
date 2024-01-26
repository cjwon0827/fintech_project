package zerobase.fintech.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.fintech.component.TokenProvider;
import zerobase.fintech.dto.LoginDto;
import zerobase.fintech.dto.MemberDto;
import zerobase.fintech.dto.PasswordCheckDto;
import zerobase.fintech.dto.UserUpdateDto;
import zerobase.fintech.entity.Member;
import zerobase.fintech.service.MemberService;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;
  private final TokenProvider tokenProvider;

  /**
   * 회원가입을 위한 기능, MemberDto에 회원 정보 받음
   * @param memberDto
   * @return
   */
  @PostMapping("/create/member")
  public ResponseEntity<?> createMember(@RequestBody MemberDto memberDto) {
    Member result = memberService.createMember(memberDto);
    return ResponseEntity.ok(result);
  }


  /**
   * 로그인을 위한 기능, LoginDto에 로그인을 위한 정보 받음
   * @param loginDto
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
    Member member = memberService.login(loginDto);
    String token = tokenProvider.generateToken(member.getEmail(), String.valueOf(member.getRole()));

    Map<String, Object> result = new HashMap<>();
    result.put("status", "로그인 성공");
    result.put("token", token);
    return ResponseEntity.ok(result);
  }

  /**
   * 회원가입 정보 입력 후 메일 인증을 위한 기능
   * @param uuid
   * @return
   */
  @GetMapping("/member/email-auth")
  public String emailAuth(@RequestParam("id") String uuid) {
    boolean result = memberService.emailAuth(uuid);
    if (result) {
      return "인증 완료, 회원 가입 완료";
    }

    return "이미 인증 되었거나 인증이 확인되지 않습니다.";
  }


  /**
   * 회원 정보 수정을 위한 기능, UserUpdateDto에 회원 정보 수정 데이터 받음
   * @param userUpdateDto
   * @param email
   * @return
   */
  @PutMapping("/update/member/{email}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updateUserInfo(@RequestBody UserUpdateDto userUpdateDto, @PathVariable String email){
    Member result = memberService.updateUserInfo(email, userUpdateDto);
    return ResponseEntity.ok(result);
  }

  /**
   * 회원 정보 수정 전 비밀번호 체크 기능, PasswordCheckDto에 비밀번호 정보 받음
   * @param passwordCheckDto
   * @param email
   * @return
   */
  @PostMapping("/member/passwordCheck/{email}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updateUserInfoPasswordCheck(@RequestBody PasswordCheckDto passwordCheckDto, @PathVariable String email){
    memberService.updateUserInfoPasswordCheck(email, passwordCheckDto);
    Map<String, Object> result = new HashMap<>();
    result.put("status", "비밀번호 확인 완료");

    return ResponseEntity.ok(result);
  }


  /**
   * 회원 정보 삭제 기능, PasswordCheckDto에 비밀번호 정보 받음
   * @param passwordCheckDto
   * @param email
   * @return
   */
  @DeleteMapping("/delete/member/{email}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> deleteUserInfo(@RequestBody PasswordCheckDto passwordCheckDto, @PathVariable String email){
    String deletedEmail = memberService.deleteUserInfo(email, passwordCheckDto);

    Map<String, Object> result = new HashMap<>();
    result.put("status", "삭제 성공");
    result.put("deletedEmail", deletedEmail);

    return ResponseEntity.ok(result);
  }
}
