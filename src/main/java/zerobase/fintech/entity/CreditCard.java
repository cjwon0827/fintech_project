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
@Entity(name = "credit_card")
public class CreditCard {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long cardId;

  @Column(name = "card_num", unique = true)
  private String cardNum;

  @Column(name = "card_password")
  private String cardPassword;

  @Column(name = "card_owner")
  private String cardOwner;

  @Column(name = "usage_amount")
  private int usageAmount;

  @Column(name = "limit_amount")
  private int limitAmount;

  @Column(name = "expiration_year")
  private int expirationYear;

  @Column(name = "monthly_payment_day")
  private int monthlyPaymentDay;

  @Column(name = "payment_date")
  private LocalDateTime paymentDate;

  @Column(name = "create_card_date")
  private LocalDateTime createCardDate;

  @Column(name = "expiration_date")
  private LocalDateTime expirationDate;

  @Column(name = "payment_yn")
  private boolean paymentYn;

  @Column(name = "card_available")
  private boolean cardAvailable;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;
}