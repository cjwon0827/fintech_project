package zerobase.fintech.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import zerobase.fintech.dto.request.card.CardHistoryDto;
import zerobase.fintech.dto.request.card.CreateCardDto;
import zerobase.fintech.dto.request.card.DeleteCardDto;
import zerobase.fintech.dto.request.card.FindCardByAccountDto;
import zerobase.fintech.dto.request.card.FindCardByMemberDto;
import zerobase.fintech.dto.request.card.PaymentCardDto;
import zerobase.fintech.dto.request.card.UseCardDto;
import zerobase.fintech.dto.response.card.CreateCardResponse;
import zerobase.fintech.dto.response.card.DeleteCardResponse;
import zerobase.fintech.dto.response.card.FindCardResponse;
import zerobase.fintech.dto.response.card.ImmediatelyPaymentResponse;
import zerobase.fintech.dto.response.card.UsageCardResponse;
import zerobase.fintech.entity.CreditCard;
import zerobase.fintech.entity.Transaction;
import zerobase.fintech.repository.CreditCardRepository;
import zerobase.fintech.service.CreditCardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/credit-card")
public class CreditCardController {
  private final CreditCardService creditCardService;
  private final CreditCardRepository creditCardRepository;


  /**
   * 카드 생성 하는 API
   * @param createCardDto
   * @return
   */
  @PostMapping
  public ResponseEntity<?> createCard(@Validated @RequestBody CreateCardDto createCardDto){
    CreditCard creditCard = creditCardService.createCard(createCardDto);
    return ResponseEntity.ok(CreateCardResponse.response(creditCard.getCardNum()));
  }


  /**
   * 해당 계정의 모든 카드 내역 정보 조회 하는 API
   * @param findCardByMemberDto
   * @param page
   * @return
   */
  @GetMapping("/members")
  public ResponseEntity<?> findCardByMember(@Validated @RequestBody FindCardByMemberDto findCardByMemberDto, @RequestParam(value = "page", defaultValue = "0") int page){
    List<CreditCard> creditCardList = creditCardService.cardInfoByMember(findCardByMemberDto, page);
    return findCard(creditCardList);
  }


  /**
   * 해당 계좌와 연동된 카드 내역 정보 조회 하는 API
   * @param findCardByAccountDto
   * @param page
   * @return
   */
  @GetMapping("/accounts")
  public ResponseEntity<?> findCardByAccount(@Validated @RequestBody FindCardByAccountDto findCardByAccountDto, @RequestParam(value = "page", defaultValue = "0") int page){
    List<CreditCard> creditCardList = creditCardService.cardInfoByAccount(findCardByAccountDto, page);
    return findCard(creditCardList);
  }


  /**
   * 카드 사용을 위한 API
   * @param useCardDto
   * @return
   */
  @PostMapping("/usage")
  public ResponseEntity<?> usageCard(@Validated @RequestBody UseCardDto useCardDto){
    Transaction transaction = creditCardService.usageCard(useCardDto);
    return ResponseEntity.ok(UsageCardResponse.response(transaction.getTransactionAmount()));
  }


  /**
   * 카드 사용 내역, 결제 예정 금액 및 카드 상태를 조회 하는 API
   * @param cardHistoryDto
   * @param page
   * @return
   */
  @GetMapping("/usage")
  public ResponseEntity<?> cardUsageInfo(@Validated @RequestBody CardHistoryDto cardHistoryDto, @RequestParam(value = "page", defaultValue = "0") int page){
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
   * @return
   */
  @PostMapping("/pay")
  public ResponseEntity<?> immediatelyCardPayment(@Validated @RequestBody PaymentCardDto paymentCardDto) {
    CreditCard creditCard = creditCardService.immediatelyCardPayment(paymentCardDto);
    return ResponseEntity.ok(ImmediatelyPaymentResponse.response(paymentCardDto.getAmount(), creditCard.getUsageAmount()));
  }


  /**
   * 카드 해지(삭제) 하는 API
   * @param deleteCardDto
   * @return
   */
  @DeleteMapping
  public ResponseEntity<?> deleteCard(@Validated @RequestBody DeleteCardDto deleteCardDto){
    String deletedCard = creditCardService.deleteCard(deleteCardDto);
    return ResponseEntity.ok(DeleteCardResponse.response(deletedCard));
  }

  private ResponseEntity<?> findCard(List<CreditCard> creditCardList) {
    if (creditCardList.isEmpty()){
      return ResponseEntity.ok("해당 회원은 개설 된 카드가 없습니다.");
    }

    List<FindCardResponse> creditCardResult = new ArrayList<>();

    for (CreditCard creditCard : creditCardList){
      FindCardResponse response = FindCardResponse.response(creditCard);
      creditCardResult.add(response);
    }

    return ResponseEntity.ok(creditCardResult);
  }
}
