package bridge.bridge_bank.domain.transfer_transaction_result;

import bridge.bridge_bank.domain.transfer_transaction_result.dto.TransferTransactionResultTargetOption;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import bridge.bridge_bank.domain.transfer_transaction_result.repository.TransferTransactionResultQueryRepository;
import bridge.bridge_bank.domain.transfer_transaction_result.repository.TransferTransactionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferTransactionResultService {
    private final TransferTransactionResultRepository transactionResultRepository;
    private final TransferTransactionResultQueryRepository transferTransactionResultQueryRepository;

    @Transactional
    public void saveTransferTransactionResultBoth(
            TransferTransactionResult transferTransactionResult1,
            TransferTransactionResult transferTransactionResult2
    ) {
        List<TransferTransactionResult> transferTransactionResults = new ArrayList<>();
        transferTransactionResults.add(transferTransactionResult1);
        transferTransactionResults.add(transferTransactionResult2);
        transactionResultRepository.saveAll(transferTransactionResults);
    }

    //todo:유형별 최근 계좌 거래 내역 10개 조회 기능 은서한테 맡기기
    @Transactional(readOnly = true)
    public List<TransferTransactionResult> get10TransferTransactionResults(
            String senderAccountNumber,
            TransferTransactionResultTargetOption transferTransactionResultTargetOption
    ){
        return transferTransactionResultQueryRepository.get10TransferTransactionResults(
                senderAccountNumber, transferTransactionResultTargetOption
        );
    }
}
