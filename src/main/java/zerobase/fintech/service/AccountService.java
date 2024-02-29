package zerobase.fintech.service;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.fintech.dto.request.account.CreateAccountDto;
import zerobase.fintech.dto.request.account.DeleteAccountDto;
import zerobase.fintech.dto.request.account.DepositWithdrawDto;
import zerobase.fintech.dto.request.account.FindAccountDto;
import zerobase.fintech.entity.Account;
import zerobase.fintech.entity.Member;
import zerobase.fintech.entity.Transaction;
import zerobase.fintech.exception.account.AccountPasswordFormatException;
import zerobase.fintech.exception.account.ExistBalanceException;
import zerobase.fintech.exception.account.InsufficientBalanceException;
import zerobase.fintech.exception.account.LimitCreateAccountException;
import zerobase.fintech.exception.account.NotExistAccountException;
import zerobase.fintech.exception.member.NotExistEmailException;
import zerobase.fintech.exception.member.NotSamePasswordException;
import zerobase.fintech.repository.AccountRepository;
import zerobase.fintech.repository.MemberRepository;
import zerobase.fintech.repository.TransactionRepository;
import zerobase.fintech.type.TransactionType;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final BCryptPasswordEncoder passwordEncoder;
  private final AccountRepository accountRepository;
  private final MemberRepository memberRepository;
  private final TransactionRepository transactionRepository;


  /**
   * 계좌 생성 기능 서비스
   * 1. 계좌를 생성할 때는 계정의 비밀번호와 계좌에 설정 할 비밀번호를 입력 받음
   * 2. 계정이 존재 하지 않는 경우 예외 발생(NotExistEmailException())
   * 3. 계좌의 비밀 번호는 숫자 4자리만 가능, 조건에 일치하지 않을 시 예외 발생
   *    (AccountPasswordLengthException(), AccountPasswordFormatException())
   * 4. 한 계정 당 최대 10개 까지만 계좌를 생성할 수 있다. 10개 초과 시 예외발생
   *    (LimitCreateAccountException())
   * @param email
   * @param accountDto
   * @return
   */
  @Transactional
  public Account createAccount(String email, CreateAccountDto accountDto) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotExistEmailException());

    if (!passwordEncoder.matches(accountDto.getMemberPassword(), member.getPassword())) {
      throw new NotSamePasswordException();
    }

    try {
      int accountPassword = Integer.parseInt(accountDto.getAccountPassword());
    } catch (NumberFormatException e){
      throw new AccountPasswordFormatException();
    }

    Long accounts = accountRepository.countByMember_MemberId(member.getMemberId());
    if(accounts >= 10){
      throw new LimitCreateAccountException();
    }

    String accountPassword = passwordEncoder.encode(accountDto.getAccountPassword());
    Account account = Account.builder()
        .accountNum(getAccountNum())
        .accountPassword(accountPassword)
        .createdAt(LocalDateTime.now())
        .member(member)
        .balance(0)
        .build();

    accountRepository.save(account);
    return account;
  }

  /**
   * 계좌 해지 기능 서비스
   * 1. 계좌를 해지하기 위해 사용자는 계정의 비밀번호, 계좌 번호, 계좌 비밀번호를 입력받음
   * 2. 계좌의 잔액이 존재하는 경우 해당 계좌는 해지할 수 없다. (ExistBalanceException())
   * 3. 계좌가 정상적으로 삭제 된 경우 해당 계좌번호를 return
   * @param email
   * @param deleteAccountDto
   * @return
   */
  @Transactional
  public String deleteAccount(String email, DeleteAccountDto deleteAccountDto) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotExistEmailException());

    if (!passwordEncoder.matches(deleteAccountDto.getMemberPassword(), member.getPassword())) {
      throw new NotSamePasswordException();
    }

    Account account = accountRepository.findByAccountNum(deleteAccountDto.getAccountNum())
        .orElseThrow(() -> new NotExistAccountException());

    if (account.getBalance() > 0) {
      throw new ExistBalanceException();
    }

    String accountNum = account.getAccountNum();
    accountRepository.deleteById(account.getAccountId());

    return accountNum;
  }

  /**
   * 계좌 조회 기능 서비스
   * 1. 사용자는 계정의 비밀번호를 입력 해야 계좌를 조회할 수 있음
   * 2. 계좌는 1페이지 당 최대 5개까지 조회 됨
   * @param email
   * @param accountDto
   * @param page
   * @return
   */
  public List<Account> findAccount(String email, FindAccountDto accountDto, int page) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotExistEmailException());

    if (!passwordEncoder.matches(accountDto.getMemberPassword(), member.getPassword())) {
      throw new NotSamePasswordException();
    }

    Pageable limit = PageRequest.of(page, 5, Sort.by(Direction.DESC, "balance"));
    Page<Account> accountPage = accountRepository.findAllByMember_MemberId(limit, member.getMemberId());

    return accountPage.stream().collect(Collectors.toList());
  }


  /**
   * 계좌 입금 기능 서비스
   * 1. 사용자는 계좌번호와 입금 금액과 계좌 비밀번호를 입력한다.
   * 2. 입금에 성공하면 기존 금액 + 입금 금액이 계좌에 저장
   * @param accountNum
   * @param depositWithdrawDto
   * @return
   */
  @Transactional
  public Account depositAccount(String accountNum, DepositWithdrawDto depositWithdrawDto) {
    Account account = accountRepository.findByAccountNum(accountNum)
        .orElseThrow(() -> new NotExistAccountException());

    if (!passwordEncoder.matches(depositWithdrawDto.getAccountPassword(), account.getAccountPassword())){
      throw new NotSamePasswordException();
    }

    account.setBalance(account.getBalance() + depositWithdrawDto.getAmount());

    Transaction transaction = Transaction.builder()
        .transactionAmount(depositWithdrawDto.getAmount())
        .transactionBalance(account.getBalance() + depositWithdrawDto.getAmount())
        .destinationAccount(accountNum)
        .transactionDate(LocalDateTime.now())
        .transactionType(TransactionType.DEPOSIT.name())
        .account(account)
        .build();

    transactionRepository.save(transaction);
    return account;
  }

  /**
   * 계좌 출금 기능 서비스
   * 1. 사용자는 계좌번호와 출금 금액과 계좌 비밀번호를 입력한다.
   * 2. 사용자는 잔고 금액보다 더 큰 금액을 출금할 수 없다. (InsufficientBalanceException())
   * 3. 출금에 성공하면 기존 금액 - 출금 금액이 계좌에 저장
   * @param accountNum
   * @param depositWithdrawDto
   * @return
   */

  @Transactional
  public Account withdrawAccount(String accountNum, DepositWithdrawDto depositWithdrawDto) {
    Account account = accountRepository.findByAccountNum(accountNum)
        .orElseThrow(() -> new NotExistAccountException());

    if (!passwordEncoder.matches(depositWithdrawDto.getAccountPassword(), account.getAccountPassword())){
      throw new NotSamePasswordException();
    }


    if (account.getBalance() - depositWithdrawDto.getAmount() < 0){
      throw new InsufficientBalanceException();
    }

    account.setBalance(account.getBalance() - depositWithdrawDto.getAmount());

    Transaction transaction = Transaction.builder()
        .transactionAmount(depositWithdrawDto.getAmount())
        .transactionBalance(account.getBalance() - depositWithdrawDto.getAmount())
        .destinationAccount(accountNum)
        .transactionDate(LocalDateTime.now())
        .transactionType(TransactionType.WITHDRAW.name())
        .account(account)
        .build();

    transactionRepository.save(transaction);
    return account;
  }

  /**
   * 계좌번호 자동 생성 기능
   * @return
   */
  public String getAccountNum() {
    Random random = new Random();
    String accountNum = "";
    int digit = 12;

    for (int i = 0; i < digit; i++) {
      int createNum = random.nextInt(10);
      String randomNum = String.valueOf(createNum);
      accountNum += randomNum;
    }

    return accountNum;
  }
}
