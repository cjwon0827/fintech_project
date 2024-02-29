package zerobase.fintech.dto.response.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import zerobase.fintech.entity.Transaction;

@Builder
@Getter
@AllArgsConstructor
public class TransferResponse {
  private String status;
  private String receiveAccount;
  private int transferAmount;

  public static TransferResponse response(Transaction transaction){
    return TransferResponse.builder()
        .status(transaction.getReceiveName() + "님께 송금 성공")
        .receiveAccount(transaction.getDestinationAccount())
        .transferAmount(transaction.getTransactionAmount())
        .build();
  }
}
