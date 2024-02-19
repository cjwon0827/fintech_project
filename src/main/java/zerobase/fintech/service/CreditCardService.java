package zerobase.fintech.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.fintech.dto.card.CardHistoryDto;
import zerobase.fintech.dto.card.CreateCardDto;
import zerobase.fintech.dto.card.DeleteCardDto;
import zerobase.fintech.dto.card.FindCardByAccountDto;
import zerobase.fintech.dto.card.FindCardByMemberDto;
import zerobase.fintech.dto.card.PaymentCardDto;
import zerobase.fintech.dto.card.UseCardDto;
import zerobase.fintech.entity.Account;
import zerobase.fintech.entity.CreditCard;
import zerobase.fintech.entity.Member;
import zerobase.fintech.entity.Transaction;
import zerobase.fintech.exception.account.AccountPasswordFormatException;
import zerobase.fintech.exception.account.InsufficientBalanceException;
import zerobase.fintech.exception.account.NotExistAccountException;
import zerobase.fintech.exception.card.CanNotDeleteCardException;
import zerobase.fintech.exception.card.InsufficientMinimumBalanceException;
import zerobase.fintech.exception.card.LimitCreateCardException;
import zerobase.fintech.exception.card.LimitExcessException;
import zerobase.fintech.exception.card.NotExceedCardUsageAmountException;
import zerobase.fintech.exception.card.NotExistCardNumException;
import zerobase.fintech.exception.card.StopCardException;
import zerobase.fintech.exception.member.NotExistEmailException;
import zerobase.fintech.exception.member.NotSamePasswordException;
import zerobase.fintech.repository.AccountRepository;
import zerobase.fintech.repository.CreditCardRepository;
import zerobase.fintech.repository.MemberRepository;
import zerobase.fintech.repository.TransactionRepository;
import zerobase.fintech.type.TransactionType;

@Service
@RequiredArgsConstructor
public class CreditCardService {
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final MemberRepository memberRepository;
  private final CreditCardRepository creditCardRepository;
  private final BCryptPasswordEncoder passwordEncoder;


  /**
   * 카드 생성을 위한 기능 서비스
   * 1. 카드 생성을 위해서는 연동 계좌, 계좌 비밀번호, 카드 비밀번호, 한도, 카드결제일(1~31), 유효기간 년수(1~5)를 입력받음
   * 2. 카드 비밀번호는 숫자 4자리만 사용 가능함
   *    이 외에 다른 형식의 값이 들어오면 예외 발생(AccountPasswordFormatException())
   * 3. 현재 계좌 잔액이 50만원 이상 이여만 카드 생성 가능
   *    50만원 미만의 잔액 시 예외 발생(InsufficientMinimumBalanceException())
   * 4. 한 계좌 당 하나의 카드만 개설 가능, 여러 개의 카드를 개설 하려고 할 시 예외 발생
   *    (LimitCreateCardException())
   * @param createCardDto
   * @return
   */
  @Transactional
  public CreditCard createCard(CreateCardDto createCardDto) {
    Account account = accountRepository.findByAccountNum(createCardDto.getLinkedAccount())
        .orElseThrow(() -> new NotExistAccountException());

    if (!passwordEncoder.matches(createCardDto.getAccountPassword(), account.getAccountPassword())) {
      throw new NotSamePasswordException();
    }

    try {
      int cardPassword = Integer.parseInt(createCardDto.getCardPassword());
    } catch (NumberFormatException e){
      throw new AccountPasswordFormatException();
    }

    int minimumBalance = account.getBalance();
    if(minimumBalance < 500000){
      throw new InsufficientMinimumBalanceException();
    }

    Long card = creditCardRepository.countByAccount_AccountId(account.getAccountId());
    if(card > 0){
      throw new LimitCreateCardException();
    }

    Member member = memberRepository.getById(account.getMember().getMemberId());

    String cardPassword = passwordEncoder.encode(createCardDto.getCardPassword());
    CreditCard creditCard = CreditCard.builder()
        .cardNum(getCardNum())
        .cardPassword(cardPassword)
        .cardOwner(member.getUserName())
        .usageAmount(0)
        .limitAmount(createCardDto.getLimitAmount())
        .expirationYear(createCardDto.getExpirationYear())
        .monthlyPaymentDay(createCardDto.getMonthlyDay())
        .createCardDate(LocalDateTime.now())
        .expirationDate(LocalDateTime.now().plusYears(createCardDto.getExpirationYear()))
        .paymentYn(true)
        .cardAvailable(true)
        .member(member)
        .account(account)
        .build();

    creditCardRepository.save(creditCard);
    return creditCard;
  }

  /**
   * 해당 계정의 모든 카드 내역을 조회하는 서비스
   * 1. 카드 내역 조회를 위해 이메일(아이디)와 계정 비밀번호를 입력 받음
   * 2. 카드 생성 날짜 내림 차순으로 한 페이지 당 5개의 내역을 조회함
   * @param findCardByMemberDto
   * @param page
   * @return
   */
  public List<CreditCard> cardInfoByMember(FindCardByMemberDto findCardByMemberDto, int page) {
    Member member = memberRepository.findByEmail(findCardByMemberDto.getEmail())
        .orElseThrow(() -> new NotExistEmailException());

    if (!passwordEncoder.matches(findCardByMemberDto.getMemberPassword(), member.getPassword())) {
      throw new NotSamePasswordException();
    }

    Pageable limit = PageRequest.of(page, 5, Sort.by(Direction.DESC, "createCardDate"));
    Page<CreditCard> creditCardPage = creditCardRepository.findAllByMember_MemberId(limit, member.getMemberId());

    return creditCardPage.stream().collect(Collectors.toList());
  }


  /**
   * 해당 계좌와 연동된 카드 내역 정보 조회하는 서비스
   * 1. 카드 내역 조회를 위해 계좌번호와 계좌 비밀번호를 입력 받음
   * 2. 카드 생성 날짜 내림 차순으로 한 페이지 당 5개의 내역을 조회함
   * @param findCardByAccountDto
   * @param page
   * @return
   */
  public List<CreditCard> cardInfoByAccount(FindCardByAccountDto findCardByAccountDto, int page) {
    Account account = accountRepository.findByAccountNum(findCardByAccountDto.getAccountNum())
        .orElseThrow(() -> new NotExistAccountException());

    if (!passwordEncoder.matches(findCardByAccountDto.getAccountPassword(), account.getAccountPassword())) {
      throw new NotSamePasswordException();
    }

    Pageable limit = PageRequest.of(page, 5, Sort.by(Direction.DESC, "createCardDate"));
    Page<CreditCard> creditCardPage = creditCardRepository.findAllByAccount_AccountId(limit, account.getAccountId());

    return creditCardPage.stream().collect(Collectors.toList());
  }

  /**
   * 카드 사용을 위한 서비스
   * 1. 카드 사용을 위해 카드 번호, 카드 비밀번호, 금액을 입력 받음
   * 2. 해당 카드가 정지 된 상태라면 사용할 수 없음(StopCardException())
   * 3. 해당 카드의 한도 금액보다 초과 된 금액을 사용하려고 하면 예외 발생(LimitExcessException())
   * 4. 카드 사용이 완료 되면 거래 내역에 저장
   * @param useCardDto
   * @return
   */
  @Transactional
  public Transaction usageCard(UseCardDto useCardDto) {
    CreditCard creditCard = creditCardRepository.findByCardNum(useCardDto.getCardNum())
        .orElseThrow(() -> new NotExistCardNumException());

    if(!creditCard.isCardAvailable()){
      throw new StopCardException();
    }

    if (!passwordEncoder.matches(useCardDto.getCardPassword(), creditCard.getCardPassword())) {
      throw new NotSamePasswordException();
    }

    if (creditCard.getUsageAmount() + useCardDto.getAmount() > creditCard.getLimitAmount()){
      throw new LimitExcessException();
    }

    Account account = accountRepository.getById(creditCard.getAccount().getAccountId());

    creditCard.setUsageAmount(creditCard.getUsageAmount() + useCardDto.getAmount());
    creditCard.setPaymentYn(false);
    creditCardRepository.save(creditCard);

    Transaction transaction = Transaction.builder()
        .transactionAmount(useCardDto.getAmount())
        .transactionBalance(account.getBalance())
        .startAccount(account.getAccountNum())
        .destinationAccount(account.getAccountNum())
        .transactionType(TransactionType.CARD.name())
        .transactionDate(LocalDateTime.now())
        .account(account)
        .build();

    transactionRepository.save(transaction);
    return transaction;
  }


  /**
   * 카드 사용 내역 조회를 위한 서비스
   * 1. 사용자는 카드 사용 내역 조회를 위해 카드 번호, 카드 비밀번호, 시작 날짜, 끝 날짜를 입력 받음
   * 2. 거래 날짜 내림 차순으로 한 페이지 당 5개의 내역을 조회함
   * @param cardHistoryDto
   * @param page
   * @return
   */
  public List<Transaction> cardUsageInfo(CardHistoryDto cardHistoryDto, int page) {
    CreditCard creditCard = creditCardRepository.findByCardNum(cardHistoryDto.getCardNum())
        .orElseThrow(() -> new NotExistCardNumException());

    if (!passwordEncoder.matches(cardHistoryDto.getCardPassword(), creditCard.getCardPassword())) {
      throw new NotSamePasswordException();
    }

    Pageable limit = PageRequest.of(page, 5, Sort.by(Direction.DESC, "transaction_date"));
    return transactionRepository.findCardUsageHistory(creditCard.getAccount().getAccountId(), TransactionType.CARD.name(), cardHistoryDto.getStartDate(), cardHistoryDto.getEndDate(), limit);
  }


  /**
   * 매일 오전 9시마다 전체 카드 중 오늘 날짜와 거래 날짜가 일치하는 카드가 있으면 카드 납부 진행
   * 1. 만약 연동 계좌의 잔액 보다 카드 사용량이 더 많다면
   *    계좌 잔액 만큼 납부를 진행하고 카드를 정지 상태로 만듦
   * 2. 정상적으로 납부가 됐다면 카드 사용량을 0으로 초기화하고,
   *    카드 납부 여부를 true로 변경한 뒤 거래 내역에 저장
   */
  @Scheduled(cron = "0 0 9 * * *")
  public void paymentCard(){
    List<CreditCard> cardList = creditCardRepository.findAll();
    int dayOfMonth = LocalDate.now().getDayOfMonth();

    for(CreditCard creditCard : cardList){
      if (creditCard.getMonthlyPaymentDay() == dayOfMonth && creditCard.isCardAvailable()) {
        Account account = accountRepository.getById(creditCard.getAccount().getAccountId());

        if (account.getBalance() - creditCard.getUsageAmount() < 0){
          account.setBalance(0);
          creditCard.setUsageAmount(creditCard.getUsageAmount() - account.getBalance());
          creditCard.setCardAvailable(false);
        } else {
          account.setBalance(account.getBalance() - creditCard.getUsageAmount());
          creditCard.setUsageAmount(0);
          creditCard.setPaymentYn(true);
        }
        creditCard.setPaymentDate(LocalDateTime.now());
        creditCardRepository.save(creditCard);

        Transaction transaction = Transaction.builder()
            .transactionAmount(creditCard.getUsageAmount())
            .transactionBalance(0)
            .transactionType(TransactionType.MONTHLY_PAYMENT_CARD.name())
            .startAccount(account.getAccountNum())
            .transactionDate(LocalDateTime.now())
            .account(account)
            .build();

        transactionRepository.save(transaction);
      }
    }
  }


  /**
   * 카드 즉시 납부 서비스
   * 1. 카드 즉시 납부를 위해 카드번호, 카드 비밀번호, 납부 금액을 입력 받음
   * 2. 카드 사용량 보다 큰 금액을 납부 하려는 경우 예외 발생(NotExceedCardUsageAmountException())
   * 3. 계좌 잔액 보다 큰 금액을 납부 하려는 경우 예외 발생(InsufficientBalanceException())
   * 4. 카드 정지자가 연체 된 카드 사용량을 즉시 납부하는 경우 해당 카드는 정지가 해제됨
   * 5. 카드 즉시 납부가 정상적으로 진행 되면 거래 내역에 저장
   * @param paymentCardDto
   * @return
   */
  @Transactional
  public CreditCard immediatelyCardPayment(PaymentCardDto paymentCardDto) {
    CreditCard creditCard = creditCardRepository.findByCardNum(paymentCardDto.getCardNum())
        .orElseThrow(() -> new NotExistCardNumException());

    if (!passwordEncoder.matches(paymentCardDto.getCardPassword(), creditCard.getCardPassword())) {
      throw new NotSamePasswordException();
    }

    if (paymentCardDto.getAmount() > creditCard.getUsageAmount()) {
      throw new NotExceedCardUsageAmountException();
    }

    Account account = accountRepository.getById(creditCard.getAccount().getAccountId());
    if (account.getBalance() < paymentCardDto.getAmount()) {
      throw new InsufficientBalanceException();
    }

    account.setBalance(account.getBalance() - paymentCardDto.getAmount());
    account.setUpdatedAt(LocalDateTime.now());
    accountRepository.save(account);

    if (!creditCard.isCardAvailable()) {
      if (creditCard.getUsageAmount() - paymentCardDto.getAmount() == 0) {
        creditCard.setUsageAmount(0);
        creditCard.setCardAvailable(true);
        creditCard.setPaymentYn(true);
      } else {
        creditCard.setUsageAmount(creditCard.getUsageAmount() - paymentCardDto.getAmount());
      }
      creditCard.setPaymentDate(LocalDateTime.now());
    } else {
      if (creditCard.getUsageAmount() - paymentCardDto.getAmount() == 0){
        creditCard.setUsageAmount(0);
        creditCard.setPaymentYn(true);
      } else {
        creditCard.setUsageAmount(creditCard.getUsageAmount() - paymentCardDto.getAmount());
      }
      creditCard.setPaymentDate(LocalDateTime.now());
    }

    creditCardRepository.save(creditCard);

    Transaction transaction = Transaction.builder()
        .startAccount(account.getAccountNum())
        .transactionAmount(paymentCardDto.getAmount())
        .transactionBalance(account.getBalance())
        .account(account)
        .transactionType(TransactionType.IMMEDIATELY_PAYMENT_CARD.name())
        .transactionDate(LocalDateTime.now())
        .build();

    transactionRepository.save(transaction);
    return creditCard;
  }


  /**
   * 카드를 해지(삭제)하는 서비스
   * 1. 카드 해지를 위해 카드번호, 카드 비밀번호, 연동 계좌 비밀번호를 입력 받음
   * 2. 카드 사용량이 모두 납부가 되지 않았거나, 카드가 정지된 상태라면 카드를 해지할 수 없음(CanNotDeleteCardException())
   * 3. 정상적으로 카드가 삭제 완료되었다면 삭제 된 카드 번호 return
   * @param deleteCardDto
   * @return
   */
  @Transactional
  public String deleteCard(DeleteCardDto deleteCardDto) {
    CreditCard creditCard = creditCardRepository.findByCardNum(deleteCardDto.getCardNum())
        .orElseThrow(() -> new NotExistCardNumException());

    if (!passwordEncoder.matches(deleteCardDto.getCardPassword(), creditCard.getCardPassword())) {
      throw new NotSamePasswordException();
    }

    Account account = accountRepository.getById(creditCard.getAccount().getAccountId());

    if (!passwordEncoder.matches(deleteCardDto.getAccountPassword(), account.getAccountPassword())) {
      throw new NotSamePasswordException();
    }

    if (!creditCard.isPaymentYn() || !creditCard.isCardAvailable() || creditCard.getUsageAmount() > 0) {
      throw new CanNotDeleteCardException();
    }

    String cardNum = creditCard.getCardNum();
    creditCardRepository.deleteById(creditCard.getCardId());

    return cardNum;
  }


  /**
   * 16자리의 카드 번호를 생성하는 함수
   * @return
   */
  public String getCardNum() {
    Random random = new Random();
    String cardNum = "";
    int digit = 16;

    for (int i = 0; i < digit; i++) {
      int createNum = random.nextInt(10);
      String randomNum = String.valueOf(createNum);
      cardNum += randomNum;
    }

    return cardNum;
  }
}
