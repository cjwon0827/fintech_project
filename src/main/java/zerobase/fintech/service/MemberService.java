package zerobase.fintech.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.fintech.component.MailComponent;
import zerobase.fintech.dto.LoginDto;
import zerobase.fintech.dto.MemberDto;
import zerobase.fintech.dto.PasswordCheckDto;
import zerobase.fintech.dto.UserUpdateDto;
import zerobase.fintech.entity.Member;
import zerobase.fintech.exception.member.AlreadyExistUserException;
import zerobase.fintech.exception.member.CheckPasswordException;
import zerobase.fintech.exception.member.MemberNotEmailAuthException;
import zerobase.fintech.exception.member.NotExistEmailException;
import zerobase.fintech.exception.member.NotSamePasswordException;
import zerobase.fintech.repository.MemberRepository;
import zerobase.fintech.type.MemberRole;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final MailComponent mailComponent;


  /**
   * 1. 회원 가입 기능, 이메일이 중복되는 경우 예외 발생(AlreadyExistUserException())
   * 2. 회원 정보를 모두 입력하면 메일 인증을 위한 메일 발송
   * @param memberDto
   * @return
   */
  @Transactional
  public Member createMember(MemberDto memberDto) {
    boolean exist = memberRepository.existsByEmail(memberDto.getEmail());

    if(exist){
      throw new AlreadyExistUserException();
    }

    String encPassword = passwordEncoder.encode(memberDto.getPassword());
    String authKey = UUID.randomUUID().toString();

    Member member = Member.builder()
        .email(memberDto.getEmail())
        .userName(memberDto.getUserName())
        .password(encPassword)
        .phone(memberDto.getPhone())
        .emailAuthKey(authKey)
        .emailAuthYN(false)
        .updatePasswordCheck(false)
        .build();

    memberRepository.save(member);

    String email = member.getEmail();
    String subject = "fintech 회원가입 이메일 인증";
    String text = "<p>안녕하세요 fintech 입니다.</p><p>인증을 위하여 아래 링크를 클릭하여 인증을 완료해주세요.</p>" +
        "<a href=http://localhost:8080/member/email-auth?id=" + authKey + ">인증완료</a>";

    mailComponent.sendMail(email, subject, text);

    return member;
  }

  /**
   * 로그인 기능
   * 1. 등록 된 이메일이 없으면 예외 발생(NotExistEmailException())
   * 2. 등록 된 이메일은 있는데 비밀번호가 일치하지 않으면 예외 발생(NotSamePasswordException())
   * @param loginDto
   * @return
   */
  public Member login(LoginDto loginDto){
    Member member = memberRepository.findByEmail(loginDto.getEmail())
        .orElseThrow(() -> new NotExistEmailException());

    if(!member.isEmailAuthYN()){
      throw new MemberNotEmailAuthException();
    }

    if(!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())){
      throw new NotSamePasswordException();
    }

    return member;
  }

  /**
   * 메일 인증을 위한 기능, 메일 인증을 완료해야만 로그인 가능
   * @param uuid
   * @return
   */
  public boolean emailAuth(String uuid){
    Optional<Member> optionalMember = memberRepository.findByEmailAuthKey(uuid);
    if(optionalMember.isEmpty()){
      return false;
    }

    Member member = optionalMember.get();
    member.setRole(MemberRole.ROLE_USER);
    member.setEmailAuthYN(true);
    member.setRegisteredAt(LocalDateTime.now());

    memberRepository.save(member);
    return true;
  }

  /**
   * 회원 정보 수정을 위한 기능, 비밀번호 확인 후 수정 가능
   * 1. 이메일이 존재하지 않을 시 예외 발생(NotExistEmailException())
   * 2. 비밀번호 확인이 되지 않을 시 예외 발생(CheckPasswordException())
   * @param email
   * @param userUpdateDto
   * @return
   */
  @Transactional
  public Member updateUserInfo(String email, UserUpdateDto userUpdateDto) {
    Member nowMember = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotExistEmailException());

    if(!nowMember.isUpdatePasswordCheck()){
      throw new CheckPasswordException();
    }

    nowMember.setUserName(userUpdateDto.getUserName());
    nowMember.setPhone(userUpdateDto.getPhone());
    nowMember.setUpdatedAt(LocalDateTime.now());
    nowMember.setUpdatePasswordCheck(false);
    memberRepository.save(nowMember);

    return nowMember;
  }

  /**
   * 회원 탈퇴를 위한 기능, 비밀번호 확인 후 일치하면 탈퇴 가능
   * 1. 이메일이 존재하지 않을 시 예외 발생(NotExistEmailException())
   * 2. 입력한 비밀 번호와 DB에 있는 비밀번호가 다를 경우 예외 발생(NotSamePasswordException())
   * @param email
   * @param passwordCheckDto
   * @return
   */
  @Transactional
  public String deleteUserInfo(String email, PasswordCheckDto passwordCheckDto) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotExistEmailException());

    if(!passwordEncoder.matches(passwordCheckDto.getPassword(), member.getPassword())){
      throw new NotSamePasswordException();
    }

    memberRepository.deleteById(member.getMemberId());
    return member.getEmail();
  }

  /**
   * 회원 정보 수정을 위한 비밀번호 확인 기능
   * 1. 이메일이 존재하지 않을 시 예외 발생(NotExistEmailException())
   * 2. 입력한 비밀 번호와 DB에 있는 비밀번호가 다를 경우 예외 발생(NotSamePasswordException())
   * @param email
   * @param passwordCheckDto
   */
  public void updateUserInfoPasswordCheck(String email, PasswordCheckDto passwordCheckDto) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotExistEmailException());


    if(!passwordEncoder.matches(passwordCheckDto.getPassword(), member.getPassword())){
      throw new NotSamePasswordException();
    }

    member.setUpdatePasswordCheck(true);
    memberRepository.save(member);
  }


  /**
   * 회원 정보 조회
   * 1. 이메일이 존재하지 않을 시 예외 발생(NotExistEmailException())
   * 2. 입력한 비밀 번호와 DB에 있는 비밀번호가 다를 경우 예외 발생(NotSamePasswordException())
   * @param email
   * @param passwordCheckDto
   * @return
   */
  public Member findUserInfo(String email, PasswordCheckDto passwordCheckDto) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotExistEmailException());


    if(!passwordEncoder.matches(passwordCheckDto.getPassword(), member.getPassword())){
      throw new NotSamePasswordException();
    }

    return member;
  }
}
