package zerobase.fintech.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.fintech.dto.card.CardHistoryDto;
import zerobase.fintech.dto.card.CreateCardDto;
import zerobase.fintech.dto.card.DeleteCardDto;
import zerobase.fintech.dto.card.FindCardByAccountDto;
import zerobase.fintech.dto.card.FindCardByMemberDto;
import zerobase.fintech.dto.card.PaymentCardDto;
import zerobase.fintech.dto.card.UseCardDto;
import zerobase.fintech.entity.CreditCard;
import zerobase.fintech.entity.Transaction;
import zerobase.fintech.repository.CreditCardRepository;
import zerobase.fintech.service.CreditCardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CreditCardController {
  private final MemberController memberController;
  private final CreditCardService creditCardService;
  private final CreditCardRepository creditCardRepository;


  /**
   * 카드 생성 하는 API
   * @param createCardDto
   * @param bindingResult
   * @return
   */
  @PostMapping
  public ResponseEntity<?> createCard(@Validated @RequestBody CreateCardDto createCardDto, BindingResult bindingResult){
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    CreditCard creditCard = creditCardService.createCard(createCardDto);
    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    result.put("status", "카드 개설 완료");
    result.put("카드 번호", creditCard.getCardNum());

    return ResponseEntity.ok(result);
  }


  /**
   * 해당 계정의 모든 카드 내역 정보 조회 하는 API
   * @param findCardByMemberDto
   * @param bindingResult
   * @param page
   * @return
   */
  @GetMapping("/members")
  public ResponseEntity<?> findCardByMember(@Validated @RequestBody FindCardByMemberDto findCardByMemberDto, BindingResult bindingResult, @RequestParam(value = "page", defaultValue = "0") int page){
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    List<CreditCard> creditCardList = creditCardService.cardInfoByMember(findCardByMemberDto, page);
    if (creditCardList.isEmpty()){
      return ResponseEntity.ok("해당 회원은 개설 된 카드가 없습니다.");
    }

    List<Map<String, Object>> creditCardResult = new ArrayList<>();

    for (CreditCard creditCard : creditCardList){
      LinkedHashMap<String, Object> result = new LinkedHashMap<>();
      result.put("카드 번호", creditCard.getCardNum());
      result.put("사용 금액", creditCard.getUsageAmount() + "원");
      result.put("사용 가능 금액", creditCard.getLimitAmount() - creditCard.getUsageAmount() + "원");
      result.put("한도", creditCard.getLimitAmount() + "원");

      creditCardResult.add(result);
    }

    return ResponseEntity.ok(creditCardResult);
  }


  /**
   * 해당 계좌와 연동된 카드 내역 정보 조회 하는 API
   * @param findCardByAccountDto
   * @param bindingResult
   * @param page
   * @return
   */
  @GetMapping("/accounts")
  public ResponseEntity<?> findCardByAccount(@Validated @RequestBody FindCardByAccountDto findCardByAccountDto, BindingResult bindingResult, @RequestParam(value = "page", defaultValue = "0") int page){
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    List<CreditCard> creditCardList = creditCardService.cardInfoByAccount(findCardByAccountDto, page);
    if (creditCardList.isEmpty()){
      return ResponseEntity.ok("해당 계좌는 개설 된 카드가 없습니다.");
    }

    List<Map<String, Object>> creditCardResult = new ArrayList<>();

    for (CreditCard creditCard : creditCardList){
      LinkedHashMap<String, Object> result = new LinkedHashMap<>();
      result.put("카드 번호", creditCard.getCardNum());
      result.put("사용 금액", creditCard.getUsageAmount() + "원");
      result.put("사용 가능 금액", creditCard.getLimitAmount() - creditCard.getUsageAmount() + "원");
      result.put("한도", creditCard.getLimitAmount() + "원");

      creditCardResult.add(result);
    }

    return ResponseEntity.ok(creditCardResult);
  }


  /**
   * 카드 사용을 위한 API
   * @param useCardDto
   * @param bindingResult
   * @return
   */
  @PostMapping("/usage")
  public ResponseEntity<?> usageCard(@Validated @RequestBody UseCardDto useCardDto, BindingResult bindingResult){
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    Transaction transaction = creditCardService.usageCard(useCardDto);

    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    result.put("status", "결제 완료");
    result.put("결제 금액", transaction.getTransactionAmount());

    return ResponseEntity.ok(result);
  }


  /**
   * 카드 사용 내역, 결제 예정 금액 및 카드 상태를 조회 하는 API
   * @param cardHistoryDto
   * @param bindingResult
   * @param page
   * @return
   */
  @GetMapping("/usage")
  public ResponseEntity<?> cardUsageInfo(@Validated @RequestBody CardHistoryDto cardHistoryDto, BindingResult bindingResult, @RequestParam(value = "page", defaultValue = "0") int page){
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }


    List<Transaction> cardUsageList = creditCardService.cardUsageInfo(cardHistoryDto, page);
    if (cardUsageList.isEmpty()){
      return ResponseEntity.ok("사용 내역이 존재 하지 않습니다.");
    }

    Optional<CreditCard> optionalCreditCard = creditCardRepository.findByCardNum(cardHistoryDto.getCardNum());
    CreditCard creditCard = optionalCreditCard.get();

    List<Map<String, Object>> cardUsageResult = new ArrayList<>();
    LinkedHashMap<String, Object> cardStatus = new LinkedHashMap<>();
    int count = 0;

    for (Transaction transaction : cardUsageList){
      LinkedHashMap<String, Object> result = new LinkedHashMap<>();

      if (count == 0){
        cardStatus.put("결제 예정 금액", creditCard.getUsageAmount() + "원");
        if (creditCard.isCardAvailable()) {
          cardStatus.put("카드 정지 여부", "사용 가능 카드");
        } else {
          cardStatus.put("카드 정지 여부", "사용 정지 카드");
        }
        cardUsageResult.add(cardStatus);
      }

      result.put("사용 금액", transaction.getTransactionAmount());
      result.put("사용 날짜", transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
      cardUsageResult.add(result);
      count++;
    }

    return ResponseEntity.ok(cardUsageResult);
  }


  /**
   * 카드 사용 내역 즉시 결제 하는 API
   * @param paymentCardDto
   * @param bindingResult
   * @return
   */
  @PostMapping("/pay")
  public ResponseEntity<?> immediatelyCardPayment(@Validated @RequestBody PaymentCardDto paymentCardDto, BindingResult bindingResult) {
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    CreditCard creditCard = creditCardService.immediatelyCardPayment(paymentCardDto);
    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    result.put("status", "결제 완료");
    result.put("즉시 결제 금액", paymentCardDto.getAmount());
    result.put("즉시 결제 가능 금액", creditCard.getUsageAmount());

    return ResponseEntity.ok(result);
  }


  /**
   * 카드 해지(삭제) 하는 API
   * @param deleteCardDto
   * @param bindingResult
   * @return
   */
  @DeleteMapping
  public ResponseEntity<?> deleteCard(@Validated @RequestBody DeleteCardDto deleteCardDto, BindingResult bindingResult){
    ResponseEntity<?> response = memberController.getErrorResponseEntity(bindingResult);
    if (response != null) {
      return response;
    }

    String deletedCard = creditCardService.deleteCard(deleteCardDto);
    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    result.put("status", deletedCard + " 카드 삭제 완료");

    return ResponseEntity.ok(result);
  }

}
