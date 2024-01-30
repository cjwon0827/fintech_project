package zerobase.fintech.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.fintech.dto.AccountDto;
import zerobase.fintech.entity.Account;
import zerobase.fintech.entity.Member;
import zerobase.fintech.exception.account.ExistBalanceException;
import zerobase.fintech.exception.account.NotExistAccountException;
import zerobase.fintech.exception.account.AlreadyExistAccountNumException;
import zerobase.fintech.exception.member.NotExistEmailException;
import zerobase.fintech.exception.member.NotSamePasswordException;
import zerobase.fintech.repository.AccountRepository;
import zerobase.fintech.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final BCryptPasswordEncoder passwordEncoder;
  private final AccountRepository accountRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public Account createAccount(String email, AccountDto accountDto) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotExistEmailException());

    if(!passwordEncoder.matches(accountDto.getPassword(), member.getPassword())){
      throw new NotSamePasswordException();
    }

    boolean exist = accountRepository.existsByAccountNum(accountDto.getAccountNum());

    if(exist){
      throw new AlreadyExistAccountNumException();
    }

    Account account = Account.builder()
        .accountNum(accountDto.getAccountNum())
        .createdAt(LocalDateTime.now())
        .member(member)
        .balance(0)
        .build();

    accountRepository.save(account);
    return account;
  }

  @Transactional
  public String deleteAccount(String email, AccountDto accountDto) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotExistEmailException());

    if(!passwordEncoder.matches(accountDto.getPassword(), member.getPassword())){
      throw new NotSamePasswordException();
    }

    Account account = accountRepository.findByAccountNum(accountDto.getAccountNum())
        .orElseThrow(() -> new NotExistAccountException());

    if(account.getBalance() > 0){
      throw new ExistBalanceException();
    }

    String accountNum = account.getAccountNum();
    accountRepository.deleteById(account.getAccountId());

    return accountNum;
  }
}
