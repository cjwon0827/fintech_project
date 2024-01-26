package zerobase.fintech.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zerobase.fintech.auth.PrincipalDetails;
import zerobase.fintech.entity.Member;
import zerobase.fintech.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Member> optionalMember = memberRepository.findByEmail(username);
    if (!optionalMember.isPresent()) {
      throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
    }

    Member member = optionalMember.get();
    return new PrincipalDetails(member);
  }
}
