package bridge.bridge_bank.domain.ledger.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LedgerEntryTest {

    @Test
    void create_shouldSetAmount() {
        LedgerEntry entry = LedgerEntry.create(
                BigDecimal.valueOf(50000),
                LedgerEntryType.DEBIT,
                LedgerBankAssetType.ASSET,
                1L
        );

        assertThat(entry.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50000));
    }

    @Test
    void create_shouldSetLedgerEntryType() {
        LedgerEntry entry = LedgerEntry.create(
                BigDecimal.valueOf(50000),
                LedgerEntryType.CREDIT,
                LedgerBankAssetType.DEBT,
                1L
        );

        assertThat(entry.getLedgerEntryType()).isEqualTo(LedgerEntryType.CREDIT);
    }

    @Test
    void create_shouldSetLedgerBankAssetType() {
        LedgerEntry entry = LedgerEntry.create(
                BigDecimal.valueOf(50000),
                LedgerEntryType.DEBIT,
                LedgerBankAssetType.ASSET,
                1L
        );

        assertThat(entry.getLedgerBankAssetType()).isEqualTo(LedgerBankAssetType.ASSET);
    }

    @Test
    void create_shouldSetLedgerVoucherId() {
        LedgerEntry entry = LedgerEntry.create(
                BigDecimal.valueOf(50000),
                LedgerEntryType.DEBIT,
                LedgerBankAssetType.ASSET,
                99L
        );

        assertThat(entry.getLedgerVoucherId()).isEqualTo(99L);
    }

    @Test
    void create_idShouldBeNull() {
        LedgerEntry entry = LedgerEntry.create(
                BigDecimal.valueOf(50000),
                LedgerEntryType.DEBIT,
                LedgerBankAssetType.ASSET,
                1L
        );

        assertThat(entry.getId()).isNull();
    }
}
