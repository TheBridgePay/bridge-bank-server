package bridge.bridge_bank.domain.legacy_transfer;

import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import bridge.bridge_bank.domain.transfer_transaction_result.repository.TransferTransactionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LegacyTransferTransactionResultService {
    private final TransferTransactionResultRepository transferTransactionResultRepository;

    @Transactional
    public void saveTransferTransactionResultBoth(
            TransferTransactionResult transferTransactionResult1,
            TransferTransactionResult transferTransactionResult2
    ) {
        transferTransactionResultRepository.saveAll(
                List.of(transferTransactionResult1, transferTransactionResult2)
        );
    }
}
