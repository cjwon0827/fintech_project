package zerobase.fintech.dto.response.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CreateCardResponse {
  private String status;
  private String cardNum;

  public static CreateCardResponse response(String cardNum){
    return CreateCardResponse.builder()
        .status("계좌 생성 성공")
        .cardNum(cardNum)
        .build();
  }
}
