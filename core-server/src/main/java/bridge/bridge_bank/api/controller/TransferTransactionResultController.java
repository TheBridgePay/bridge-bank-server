package bridge.bridge_bank.api.controller;

import bridge.bridge_bank.api.controller.docs.TransferTransactionResultControllerDocs;
import bridge.bridge_bank.api.dto.TransferTransactionResultResponse;
import bridge.bridge_bank.domain.transfer_transaction_result.TransferTransactionResultService;
import bridge.bridge_bank.domain.transfer_transaction_result.dto.TransferTransactionResultTargetOption;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransferTransactionResultController implements TransferTransactionResultControllerDocs {

    private final TransferTransactionResultService transferTransactionResultService;

    @GetMapping("/api/accounts/{accountNumber}/transfer-results")
    @Override
    public ResponseEntity<List<TransferTransactionResultResponse>> getTransferResults(
            @PathVariable String accountNumber,
            @RequestParam(required = false) String receiverAccountNumber,
            @RequestParam(required = false) TransferTransactionType transferTransactionType) {

        TransferTransactionResultTargetOption option = TransferTransactionResultTargetOption.builder()
                .receiverAccountNumber(receiverAccountNumber)
                .transferTransactionType(transferTransactionType)
                .build();

        List<TransferTransactionResult> results =
                transferTransactionResultService.get10TransferTransactionResults(accountNumber, option);

        List<TransferTransactionResultResponse> response = results == null
                ? List.of()
                : results.stream().map(TransferTransactionResultResponse::from).toList();

        return ResponseEntity.ok(response);
    }
}
