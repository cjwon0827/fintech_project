package zerobase.fintech.service;

import java.time.LocalDateTime;
import java.util.List;
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
import zerobase.fintech.dto.account.AccountDto;
import zerobase.fintech.dto.transaction.TransferDto;
import zerobase.fintech.entity.Account;
import zerobase.fintech.entity.Member;
import zerobase.fintech.entity.Transaction;
import zerobase.fintech.exception.account.InsufficientBalanceException;
import zerobase.fintech.exception.account.NotExistAccountException;
import zerobase.fintech.exception.member.NotSamePasswordException;
import zerobase.fintech.exception.transaction.NotExistReceiveAccountException;
import zerobase.fintech.exception.transaction.NotExistSendAccountException;
import zerobase.fintech.repository.AccountRepository;
import zerobase.fintech.repository.MemberRepository;
import zerobase.fintech.repository.TransactionRepository;
import zerobase.fintech.type.TransactionType;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder passwordEncoder;


  /**
   * 계좌 송금 기능 서비스
   * 1. 사용자는 본인 계좌번호, 상대 계좌번호, 송금 금액, 계좌 비밀번호를 입력 받음
   * 2. 본인 계좌번호 또는 상대 계좌번호가 존재하지 않을 경우 예외 발생(NotExistSendAccountException())
   * 3. 송금이 완료 되면 각 각의 계좌에서 송금 금액 만큼 +-가 진행된 후 계좌 정보 update 되고
   *    보낸 사람, 받은 사람 거래 내역이 각 각 추가 됨
   * @param transferDto
   * @return
   */
  @Transactional
  public Transaction transfer(TransferDto transferDto) {
    Account sendAccount = accountRepository.findByAccountNum(transferDto.getStartAccountNum())
        .orElseThrow(() -> new NotExistSendAccountException());

    Account receiveAccount = accountRepository.findByAccountNum(transferDto.getDestinationAccountNum())
        .orElseThrow(() -> new NotExistReceiveAccountException());

    Member sendMember = memberRepository.getById(sendAccount.getMember().getMemberId());
    Member receiveMember = memberRepository.getById(receiveAccount.getMember().getMemberId());

    if (!passwordEncoder.matches(transferDto.getAccountPassword(), sendAccount.getAccountPassword())) {
      throw new NotSamePasswordException();
    }

    if (sendAccount.getBalance() - transferDto.getAmount() < 0){
      throw new InsufficientBalanceException();
    }

    Transaction sendTransaction = Transaction.builder()
        .startAccount(sendAccount.getAccountNum())
        .destinationAccount(receiveAccount.getAccountNum())
        .sendName(sendMember.getUserName())
        .receiveName(receiveMember.getUserName())
        .transactionAmount(transferDto.getAmount())
        .transactionBalance(sendAccount.getBalance() - transferDto.getAmount())
        .transactionDate(LocalDateTime.now())
        .transactionType(TransactionType.ACCOUNT_TRANSFER_SEND.name())
        .account(sendAccount)
        .build();

    Transaction receiveTransaction = Transaction.builder()
        .startAccount(sendAccount.getAccountNum())
        .destinationAccount(receiveAccount.getAccountNum())
        .sendName(sendMember.getUserName())
        .receiveName(receiveMember.getUserName())
        .transactionAmount(transferDto.getAmount())
        .transactionBalance(sendAccount.getBalance() + transferDto.getAmount())
        .transactionDate(LocalDateTime.now())
        .transactionType(TransactionType.ACCOUNT_TRANSFER_RECEIVE.name())
        .account(receiveAccount)
        .build();

    sendAccount.setBalance(sendAccount.getBalance() - transferDto.getAmount());
    accountRepository.save(sendAccount);

    receiveAccount.setBalance(receiveAccount.getBalance() + transferDto.getAmount());
    accountRepository.save(receiveAccount);

    transactionRepository.save(sendTransaction);
    transactionRepository.save(receiveTransaction);

    return sendTransaction;
  }


  /**
   * 해당 계좌의 거래 내역을 조회할 수 있는 서비스
   * 1. 사용자는 계좌 번호, 계좌 비밀번호, 사용자 비밀번호를 입력 받음
   * 2. 거래 날짜 내림 차순으로 한 페이지 당 5개의 내역을 조회함
   * @param accountDto
   * @param page
   * @return
   */
  public List<Transaction> transactionInfo(AccountDto accountDto, int page) {
    Account account = accountRepository.findByAccountNum(accountDto.getAccountNum())
        .orElseThrow(() -> new NotExistAccountException());

    if (!passwordEncoder.matches(accountDto.getAccountPassword(), account.getAccountPassword())) {
      throw new NotSamePasswordException();
    }


    Pageable limit = PageRequest.of(page, 5, Sort.by(Direction.DESC, "transactionDate"));
    Page<Transaction> transactionPage = transactionRepository.findAllByAccount_AccountId(limit, account.getAccountId());

    return transactionPage.stream().collect(Collectors.toList());
  }
}
