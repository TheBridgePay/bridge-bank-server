package bridge.bridge_bank.domain.transfer_transaction_result;

import bridge.bridge_bank.domain.transfer_transaction_result.dto.TransferTransactionResultTargetOption;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import bridge.bridge_bank.domain.transfer_transaction_result.repository.TransferTransactionResultBulkInsertMapper;
import bridge.bridge_bank.domain.transfer_transaction_result.repository.TransferTransactionResultQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferTransactionResultService {
    private final TransferTransactionResultBulkInsertMapper transferTransactionResultBulkInsertMapper;
    private final TransferTransactionResultQueryRepository transferTransactionResultQueryRepository;

    @Transactional
    public void saveTransferTransactionResultBoth(
            TransferTransactionResult transferTransactionResult1,
            TransferTransactionResult transferTransactionResult2
    ) {
        transferTransactionResultBulkInsertMapper.bulkInsert(
                List.of(transferTransactionResult1, transferTransactionResult2)
        );
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
