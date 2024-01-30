package zerobase.fintech.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.fintech.dto.AccountDto;
import zerobase.fintech.entity.Account;
import zerobase.fintech.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
  private final AccountService accountService;

  @PostMapping("/create/{email}")
  public ResponseEntity<?> createAccount(@RequestBody AccountDto accountDto, @PathVariable String email){
    Account account = accountService.createAccount(email, accountDto);

    return ResponseEntity.ok(account);
  }

  @DeleteMapping("/delete/{email}")
  public ResponseEntity<?> deleteAccount(@RequestBody AccountDto accountDto, @PathVariable String email){
    String accountNum = accountService.deleteAccount(email, accountDto);

    Map<String, Object> result = new HashMap<>();
    result.put("status", "삭제 완료");
    result.put("삭제 계좌", accountNum);

    return ResponseEntity.ok(result);
  }
}
