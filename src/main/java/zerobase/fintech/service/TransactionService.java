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
