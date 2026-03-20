package bridge.bridge_bank.domain.transfer;

import bridge.bridge_bank.domain.transfer_transaction.TransferTransactionResultRepository;
import bridge.bridge_bank.domain.transfer_transaction.TransferTransactionResultService;
import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferTransactionResultServiceTest {

    @Mock
    TransferTransactionResultRepository transactionResultRepository;

    @InjectMocks
    TransferTransactionResultService transferTransactionResultService;

    @Test
    void insertTransferTransactionResults_shouldCallSaveAll() {
        List<TransferTransactionResult> results = List.of(
                TransferTransactionResult.builder().senderAccountNumber("111").receiverAccountNumber("222").build(),
                TransferTransactionResult.builder().senderAccountNumber("333").receiverAccountNumber("444").build()
        );

        transferTransactionResultService.insertTransferTransactionResults(results);

        verify(transactionResultRepository, times(1)).saveAll(results);
    }
}
