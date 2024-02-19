package zerobase.fintech.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "transaction")
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long transactionId;

  @Column(name = "start_account")
  private String startAccount;

  @Column(name = "destination_account")
  private String destinationAccount;

  @Column(name = "send_name")
  private String sendName;

  @Column(name = "receive_name")
  private String receiveName;

  @Column(name = "transaction_amount")
  private int transactionAmount;

  @Column(name = "transaction_balance")
  private int transactionBalance;

  @Column(name = "transaction_date")
  private LocalDateTime transactionDate;

  @Column(name = "transaction_type")
  private String transactionType;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;

}
