package bridge.bridge_bank.domain.ledger.entity;

import bridge.bridge_bank.domain.transfer_transaction.entity.TransferTransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LedgerVoucherTest {

    @Test
    void create_shouldSetTransferTransactionGroupId() {
        LedgerVoucher voucher = LedgerVoucher.create("TXN-001", TransferTransactionType.SIMPLE_TRANSFER);

        assertThat(voucher.getTransferTransactionGroupId()).isEqualTo("TXN-001");
    }

    @Test
    void create_shouldSetTransferTransactionType() {
        LedgerVoucher voucher = LedgerVoucher.create("TXN-001", TransferTransactionType.RESERVE_ONCE);

        assertThat(voucher.getTransferTransactionType()).isEqualTo(TransferTransactionType.RESERVE_ONCE);
    }

    @Test
    void create_shouldSetLedgerVoucherDateToNow() {
        LocalDateTime before = LocalDateTime.now();
        LedgerVoucher voucher = LedgerVoucher.create("TXN-001", TransferTransactionType.SIMPLE_TRANSFER);
        LocalDateTime after = LocalDateTime.now();

        assertThat(voucher.getLedgerVoucherDate()).isBetween(before, after);
    }

    @Test
    void create_idShouldBeNull() {
        LedgerVoucher voucher = LedgerVoucher.create("TXN-001", TransferTransactionType.SIMPLE_TRANSFER);

        assertThat(voucher.getId()).isNull();
    }
}
