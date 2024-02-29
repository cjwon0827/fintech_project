package zerobase.fintech.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.fintech.component.TokenProvider;
import zerobase.fintech.dto.request.PasswordCheckDto;
import zerobase.fintech.dto.request.member.CreateMemberDto;
import zerobase.fintech.dto.request.member.LoginDto;
import zerobase.fintech.dto.request.member.UserUpdateDto;
import zerobase.fintech.dto.response.PasswordCheckResponse;
import zerobase.fintech.dto.response.member.CreateMemberResponse;
import zerobase.fintech.dto.response.member.DeleteUserResponse;
import zerobase.fintech.dto.response.member.FindUserResponse;
import zerobase.fintech.dto.response.member.LoginResponse;
import zerobase.fintech.dto.response.member.UpdateUserResponse;
import zerobase.fintech.entity.Member;
import zerobase.fintech.service.MemberService;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;
  private final TokenProvider tokenProvider;

  /**
   * 회원가입을 위한 API
   * @param memberDto
   * @return
   */
  @PostMapping("/member/new")
  public ResponseEntity<?> createMember(@Validated @RequestBody CreateMemberDto memberDto) {
    memberService.createMember(memberDto);
    return ResponseEntity.ok(CreateMemberResponse.response());
  }


  /**
   * 로그인을 위한 API
   * @param loginDto
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@Validated @RequestBody LoginDto loginDto) {
    Member member = memberService.login(loginDto);
    String token = tokenProvider.generateToken(member.getEmail(), String.valueOf(member.getRole()));

    return ResponseEntity.ok(LoginResponse.response(token));
  }


  /**
   * 회원가입 정보 입력 후 메일 인증을 위한 API
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
   * 회원 정보 수정을 위한 API
   * @param userUpdateDto
   * @param email
   * @return
   */
  @PatchMapping("/member")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updateUserInfo(@RequestParam String email, @Validated @RequestBody UserUpdateDto userUpdateDto) {
    Member member = memberService.updateUserInfo(email, userUpdateDto);

    return ResponseEntity.ok(UpdateUserResponse.response(
                            member.getUserName(),
                            member.getPhone()));
  }

  /**
   * 회원 정보 수정 전 비밀번호 체크 기능을 위한 API
   * @param passwordCheckDto
   * @param email
   * @return
   */
  @PostMapping("/member/password-check")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updateUserInfoPasswordCheck(@RequestParam String email, @Validated @RequestBody PasswordCheckDto passwordCheckDto) {
    memberService.updateUserInfoPasswordCheck(email, passwordCheckDto);

    return ResponseEntity.ok(PasswordCheckResponse.response());
  }


  /**
   * 회원 탈퇴 API
   * @param passwordCheckDto
   * @param email
   * @return
   */
  @DeleteMapping("/member")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> deleteUserInfo(@Validated @RequestBody PasswordCheckDto passwordCheckDto, @RequestParam String email) {

    try {
      String deletedEmail = memberService.deleteUserInfo(email, passwordCheckDto);
      return ResponseEntity.ok(DeleteUserResponse.okResponse(deletedEmail));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DeleteUserResponse.errorResponse());
    }
  }

  /**
   * 회원 정보 조회 API
   * @param passwordCheckDto
   * @param email
   * @return
   */
  @GetMapping("/member")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> findUserInfo(@Validated @RequestBody PasswordCheckDto passwordCheckDto, @RequestParam String email) {
    Member member = memberService.findUserInfo(email, passwordCheckDto);

    return ResponseEntity.ok(FindUserResponse.response(
                            member.getEmail(),
                            member.getUserName(),
                            member.getPhone()));
  }
}
