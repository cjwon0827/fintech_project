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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.fintech.dto.account.AccountDto;
import zerobase.fintech.dto.transaction.TransferDto;
import zerobase.fintech.entity.Transaction;
import zerobase.fintech.service.TransactionService;
import zerobase.fintech.type.TransactionType;


@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
  private final TransactionService transactionService;
  private final MemberController memberController;


  /**
   * 계좌 송금 하는 API
   * @param transferDto
   * @param bindingResult
   * @return
   */
  @PostMapping("/money")
  public ResponseEntity<?> transfer(@Validated @RequestBody TransferDto transferDto, BindingResult bindingResult){
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    Transaction transaction = transactionService.transfer(transferDto);
    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    result.put("status", transaction.getReceiveName() + "님께 송금 성공");
    result.put("수신 계좌", transaction.getDestinationAccount());
    result.put("송금 금액", transaction.getTransactionAmount() + "원");

    return ResponseEntity.ok(result);
  }


  /**
   * 해당 계좌의 거래 내역 타입 별로 조회 하는 API
   * @param accountDto
   * @param bindingResult
   * @param page
   * @return
   */
  @GetMapping
  public ResponseEntity<?> transactionInfo(@Validated @RequestBody AccountDto accountDto, BindingResult bindingResult, @RequestParam(value = "page", defaultValue = "0") int page){
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    List<Transaction> transactionList = transactionService.transactionInfo(accountDto, page);

    if (transactionList.isEmpty()){
      return ResponseEntity.ok("거래 내역이 존재하지 않습니다.");
    }

    List<Map<String, Object>> transactionResult = new ArrayList<>();
    LinkedHashMap<String, Object> balance = new LinkedHashMap<>();
    int count = 0;

    for (Transaction transaction : transactionList){
      LinkedHashMap<String, Object> result = new LinkedHashMap<>();

      if (count == 0){
        balance.put("현재 잔액", transaction.getAccount().getBalance() + "원");
        transactionResult.add(balance);
      }

      if (transaction.getTransactionType().equals(TransactionType.ACCOUNT_TRANSFER_SEND.name())) {
        result.put("수신 계좌", transaction.getDestinationAccount());
        result.put("수신자", transaction.getReceiveName());
        result.put("송금(출금) 금액", transaction.getTransactionAmount() + "원");
        result.put("거래 일자", transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        transactionResult.add(result);
      } else if(transaction.getTransactionType().equals(TransactionType.ACCOUNT_TRANSFER_RECEIVE.name())) {
        result.put("발신 계좌", transaction.getStartAccount());
        result.put("발신자", transaction.getSendName());
        result.put("송금(입금) 금액", transaction.getTransactionAmount() + "원");
        result.put("거래 일자", transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        transactionResult.add(result);
      } else if (transaction.getTransactionType().equals(TransactionType.DEPOSIT.name())){
        result.put("입금 금액", transaction.getTransactionAmount() + "원");
        result.put("입금 시 잔액", transaction.getTransactionBalance() + "원");
        result.put("거래 일자", transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        transactionResult.add(result);
      } else if (transaction.getTransactionType().equals(TransactionType.WITHDRAW.name())){
        result.put("출금 금액", transaction.getTransactionBalance() + "원");
        result.put("출금 시 잔액", transaction.getAccount().getBalance() + "원");
        result.put("거래 일자", transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        transactionResult.add(result);
      }
      count++;
    }
    return ResponseEntity.ok(transactionResult);
  }
}
