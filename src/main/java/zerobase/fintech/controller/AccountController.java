package zerobase.fintech.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.fintech.dto.account.AccountDto;
import zerobase.fintech.dto.account.DeleteAccountDto;
import zerobase.fintech.dto.account.DepositWithdrawDto;
import zerobase.fintech.entity.Account;
import zerobase.fintech.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

  private final AccountService accountService;
  private final MemberController memberController;

  /**
   * 계좌 생성 하는 API
   *
   * @param accountDto
   * @param email
   * @return
   */
  @PostMapping("/{email}")
  public ResponseEntity<?> createAccount(@Validated @RequestBody AccountDto accountDto, BindingResult bindingResult, @PathVariable String email) {
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    Account account = accountService.createAccount(email, accountDto);
    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    result.put("status", "계좌 생성 성공");
    result.put("계좌 번호", account.getAccountNum());
    result.put("생성 시간", account.getCreatedAt());

    return ResponseEntity.ok(result);
  }

  /**
   * 계좌 조회하는 API
   *
   * @param accountDto
   * @param email
   * @param page
   * @return
   */
  @GetMapping("/{email}")
  public ResponseEntity<?> selectAccount(@Validated @RequestBody AccountDto accountDto, BindingResult bindingResult, @PathVariable String email, @RequestParam(value = "page", defaultValue = "0") int page) {
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    List<Account> accountList = accountService.findAccount(email, accountDto, page);
    List<Map<String, Object>> accountResult = new ArrayList<>();

    for (Account account : accountList) {
      LinkedHashMap<String, Object> result = new LinkedHashMap<>();

      result.put("계좌 번호", account.getAccountNum());
      result.put("잔액", account.getBalance() + "원");
      result.put("계좌 개설 시간", account.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

      accountResult.add(result);
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
  @DeleteMapping("/{email}")
  public ResponseEntity<?> deleteAccount(@Validated @RequestBody DeleteAccountDto deleteAccountDto,
      BindingResult bindingResult, @PathVariable String email) {
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    String accountNum = accountService.deleteAccount(email, deleteAccountDto);

    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    result.put("status", "계좌 삭제 완료");
    result.put("삭제 계좌", accountNum);

    return ResponseEntity.ok(result);
  }


  /**
   * 계좌 입금 하는 API
   *
   * @param depositWithdrawDto
   * @param accountNum
   * @return
   */
  @PostMapping("/deposit/{accountNum}")
  public ResponseEntity<?> depositAccount(@Validated @RequestBody DepositWithdrawDto depositWithdrawDto, BindingResult bindingResult, @PathVariable String accountNum) {
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    Account account = accountService.depositAccount(accountNum, depositWithdrawDto);

    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    result.put("입금 금액", depositWithdrawDto.getAmount() + "원");
    result.put("잔고 금액", account.getBalance() + "원");

    return ResponseEntity.ok(result);
  }

  /**
   * 계좌 출금 하는 API
   *
   * @param depositWithdrawDto
   * @param accountNum
   * @return
   */
  @PostMapping("/withdraw/{accountNum}")
  public ResponseEntity<?> withdrawAccount(@Validated @RequestBody DepositWithdrawDto depositWithdrawDto, BindingResult bindingResult, @PathVariable String accountNum) {
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    Account account = accountService.withdrawAccount(accountNum, depositWithdrawDto);

    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    result.put("출금 금액", depositWithdrawDto.getAmount() + "원");
    result.put("잔고 금액", account.getBalance() + "원");

    return ResponseEntity.ok(result);
  }
}
