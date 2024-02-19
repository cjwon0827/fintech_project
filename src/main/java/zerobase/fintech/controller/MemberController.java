package zerobase.fintech.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.fintech.component.TokenProvider;
import zerobase.fintech.dto.PasswordCheckDto;
import zerobase.fintech.dto.member.LoginDto;
import zerobase.fintech.dto.member.MemberDto;
import zerobase.fintech.dto.member.UserUpdateDto;
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
  public ResponseEntity<?> createMember(@Validated @RequestBody MemberDto memberDto, BindingResult bindingResult) {
    ResponseEntity<?> response = getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    Member result = memberService.createMember(memberDto);
    return ResponseEntity.ok(result);
  }


  /**
   * 로그인을 위한 API
   * @param loginDto
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@Validated @RequestBody LoginDto loginDto, BindingResult bindingResult) {
    ResponseEntity<?> response = getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    Member member = memberService.login(loginDto);
    String token = tokenProvider.generateToken(member.getEmail(), String.valueOf(member.getRole()));

    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    result.put("status", "로그인 성공");
    result.put("token", token);
    return ResponseEntity.ok(result);
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
  @PutMapping("/member/{email}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updateUserInfo(@Validated @RequestBody UserUpdateDto userUpdateDto, BindingResult bindingResult, @PathVariable String email) {
    ResponseEntity<?> response = getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    Member result = memberService.updateUserInfo(email, userUpdateDto);
    return ResponseEntity.ok(result);
  }

  /**
   * 회원 정보 수정 전 비밀번호 체크 기능을 위한 API
   * @param passwordCheckDto
   * @param email
   * @return
   */
  @PostMapping("/member/password-check/{email}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updateUserInfoPasswordCheck(@Validated @RequestBody PasswordCheckDto passwordCheckDto, BindingResult bindingResult, @PathVariable String email) {
    ResponseEntity<?> response = getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    memberService.updateUserInfoPasswordCheck(email, passwordCheckDto);
    Map<String, Object> result = new HashMap<>();
    result.put("status", "비밀번호 확인 완료");

    return ResponseEntity.ok(result);
  }


  /**
   * 회원 탈퇴 API
   * @param passwordCheckDto
   * @param email
   * @return
   */
  @DeleteMapping("/member/{email}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> deleteUserInfo(@Validated @RequestBody PasswordCheckDto passwordCheckDto, BindingResult bindingResult, @PathVariable String email) {
    ResponseEntity<?> response = getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    LinkedHashMap<String, Object> result = new LinkedHashMap<>();

    try {
      String deletedEmail = memberService.deleteUserInfo(email, passwordCheckDto);
      result.put("status", "삭제 성공");
      result.put("deletedEmail", deletedEmail);

    } catch (Exception e) {
      result.put("status", "삭제 실패");
      result.put("message", "현재 계정에 계좌가 존재 하여 회원 탈퇴가 불가능 합니다. 계좌 해지 후 다시 시도 하십시오.");
    }

    return ResponseEntity.ok(result);
  }


  /**
   * 회원 정보 조회 API
   * @param passwordCheckDto
   * @param bindingResult
   * @param email
   * @return
   */
  @GetMapping("/member/{email}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> findUserInfo(@Validated @RequestBody PasswordCheckDto passwordCheckDto, BindingResult bindingResult, @PathVariable String email) {
    ResponseEntity<?> response = getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    Member member = memberService.findUserInfo(email, passwordCheckDto);
    LinkedHashMap<String, Object> result = new LinkedHashMap<>();

    result.put("이메일", member.getEmail());
    result.put("이름", member.getUserName());
    result.put("전화번호", member.getPhone());

    return ResponseEntity.ok(result);
  }

  /**
   * 입력한 값을 Controller 단에서 Validation 체크 하는 API
   * @param bindingResult
   * @return
   */
  public ResponseEntity<?> getErrorResponseEntity(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      StringBuilder sb = new StringBuilder();

      bindingResult.getAllErrors().forEach(e -> {
        String message = e.getDefaultMessage();
        sb.append(message + "\n");
      });

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sb.toString());
    }
    return null;
  }
}
