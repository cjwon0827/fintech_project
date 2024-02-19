package zerobase.fintech.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zerobase.fintech.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  Page<Transaction> findAllByAccount_AccountId(Pageable pageable, Long accountId);

  @Query(value = "select * from transaction t left outer join account a on t.account_id = a.account_id\n"
      + " where a.account_id = :accountId\n"
      + " and t.transaction_type = :transactionType\n"
      + " and t.transaction_date between date(:startDate) and date_sub(date_add(:endDate, interval 1 day), interval 1 second)",
      nativeQuery = true)
  List<Transaction> findCardUsageHistory(
      @Param(value = "accountId") Long accountId,
      @Param(value = "transactionType") String type,
      @Param(value = "startDate") String startDate,
      @Param(value = "endDate") String endDate,
      Pageable pageable);
}
