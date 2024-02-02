package zerobase.fintech.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import zerobase.fintech.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByAccountNum(String accountNum);
  Page<Account> findAllByMember_MemberId(Pageable pageable, Long memberId);
  Long countByMember_MemberId(Long memberId);
}
