package zerobase.fintech.controller;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.fintech.dto.request.account.CreateAccountDto;
import zerobase.fintech.dto.request.account.DeleteAccountDto;
import zerobase.fintech.dto.request.account.DepositWithdrawDto;
import zerobase.fintech.dto.request.account.FindAccountDto;
import zerobase.fintech.dto.response.account.CreateAccountResponse;
import zerobase.fintech.dto.response.account.DeleteAccountResponse;
import zerobase.fintech.dto.response.account.DepositWithdrawResponse;
import zerobase.fintech.dto.response.account.FindAccountResponse;
import zerobase.fintech.entity.Account;
import zerobase.fintech.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

  private final AccountService accountService;

  /**
   * 계좌 생성 하는 API
   *
   * @param accountDto
   * @param email
   * @return
   */
  @PostMapping
  public ResponseEntity<?> createAccount(@Validated @RequestBody CreateAccountDto accountDto, @RequestParam String email) {
    Account account = accountService.createAccount(email, accountDto);

    return ResponseEntity.ok(CreateAccountResponse.response(account.getAccountNum(), account.getCreatedAt()));
  }

  /**
   * 계좌 조회하는 API
   *
   * @param accountDto
   * @param email
   * @param page
   * @return
   */
  @GetMapping
  public ResponseEntity<?> selectAccount(@Validated @RequestBody FindAccountDto accountDto, @RequestParam String email, @RequestParam(value = "page", defaultValue = "0") int page) {
    List<Account> accountList = accountService.findAccount(email, accountDto, page);

    if (accountList.isEmpty()){
      return ResponseEntity.ok(page + "페이지에 계좌가 존재하지 않습니다.");
    }

    List<FindAccountResponse> accountResult = new ArrayList<>();

    for (Account account : accountList) {
      FindAccountResponse response = FindAccountResponse.response(account);
      accountResult.add(response);
    }

    return ResponseEntity.ok(accountResult);
  }

  /**
   * 계좌 삭제 하는 API
   *
   * @param deleteAccountDto
   * @param email
   * @return
   */
  @DeleteMapping
  public ResponseEntity<?> deleteAccount(@Validated @RequestBody DeleteAccountDto deleteAccountDto, @RequestParam String email) {
    String accountNum = accountService.deleteAccount(email, deleteAccountDto);
    return ResponseEntity.ok(DeleteAccountResponse.response(accountNum));
  }


  /**
   * 계좌 입금 하는 API
   *
   * @param depositWithdrawDto
   * @param accountNum
   * @return
   */
  @PostMapping("/deposit")
  public ResponseEntity<?> depositAccount(@Validated @RequestBody DepositWithdrawDto depositWithdrawDto, @RequestParam String accountNum) {
    Account account = accountService.depositAccount(accountNum, depositWithdrawDto);
    return ResponseEntity.ok(DepositWithdrawResponse.response(depositWithdrawDto.getAmount(), account.getBalance()));
  }

  /**
   * 계좌 출금 하는 API
   *
   * @param depositWithdrawDto
   * @param accountNum
   * @return
   */
  @PostMapping("/withdraw")
  public ResponseEntity<?> withdrawAccount(@Validated @RequestBody DepositWithdrawDto depositWithdrawDto, @RequestParam String accountNum) {
    Account account = accountService.withdrawAccount(accountNum, depositWithdrawDto);
    return ResponseEntity.ok(DepositWithdrawResponse.response(depositWithdrawDto.getAmount(), account.getBalance()));
  }
}
