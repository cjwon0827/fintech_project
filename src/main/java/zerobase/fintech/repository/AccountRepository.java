package zerobase.fintech.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import zerobase.fintech.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
  boolean existsByAccountNum(String accountNum);
  Optional<Account> findByAccountNum(String accountNum);
}
