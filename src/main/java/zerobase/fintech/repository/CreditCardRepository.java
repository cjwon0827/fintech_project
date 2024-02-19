package zerobase.fintech.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import zerobase.fintech.entity.CreditCard;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
  Long countByAccount_AccountId(Long accountId);
  Page<CreditCard> findAllByMember_MemberId(Pageable pageable, Long memberId);
  Page<CreditCard> findAllByAccount_AccountId(Pageable pageable, Long accountId);

  Optional<CreditCard> findByCardNum(String cardNum);
}
