package bridge.bridge_bank.domain.transfer_transaction;

import bridge.bridge_bank.domain.transfer_transaction.dto.TransferTransactionResultTargetOption;
import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionResult;
import bridge.bridge_bank.domain.transfer_transaction.repository.TransferTransactionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferTransactionResultService {
    private final TransferTransactionResultRepository transactionResultRepository;

    @Transactional
    public void insertTransferTransactionResults(
            List<TransferTransactionResult> transferTransactionResults
    ) {
        transactionResultRepository.saveAll(transferTransactionResults);
    }

    //todo:유형별 최근 계좌 거래 내역 10개 조회 기능 은서한테 맡기기
    @Transactional(readOnly = true)
    public List<TransferTransactionResult> get10TransferTransactionResults(
            String senderAccountNumber,
            TransferTransactionResultTargetOption transferTransactionResultTargetOption
    ){
        return null;
    }
}
