package zerobase.fintech.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import zerobase.fintech.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
  boolean existsByEmail(String email);
  Optional<Member> findByEmail(String email);
  Optional<Member> findByEmailAuthKey(String uuid);
}
