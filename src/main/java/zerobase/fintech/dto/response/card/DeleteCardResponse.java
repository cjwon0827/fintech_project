package zerobase.fintech.dto.response.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class DeleteCardResponse {
  private String status;
  private String deletedCardNum;

  public static DeleteCardResponse response(String deletedCardNum){
    return DeleteCardResponse.builder()
        .status("카드 삭제 성공")
        .deletedCardNum(deletedCardNum)
        .build();
  }
}
