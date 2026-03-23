package bridge.bridge_bank.domain.transfer;

import bridge.bridge_bank.domain.account.AccountService;
import bridge.bridge_bank.domain.account.entity.Account;
import bridge.bridge_bank.domain.ledger.LedgerService;
import bridge.bridge_bank.domain.transfer_transaction_result.TransferTransactionResultService;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionResult;
import bridge.bridge_bank.domain.transfer_transaction_result.entity.TransferTransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceReserveTransferTest {

    @Mock
    private AccountService accountService;

    @Mock
    private TransferTransactionResultService transferTransactionResultService;

    @Mock
    private LedgerService ledgerService;

    @InjectMocks
    private TransferService transferService;

    private Account senderAccount;
    private Account receiverAccount;

    @BeforeEach
    void setUp() {
        senderAccount = Account.builder()
                .id(1L)
                .accountNumber("1111111111")
                .memberName("Sender")
                .password("1234")
                .balance(BigDecimal.valueOf(1_000_000))
                .build();

        receiverAccount = Account.builder()
                .id(2L)
                .accountNumber("2222222222")
                .memberName("Receiver")
                .password("5678")
                .balance(BigDecimal.valueOf(500_000))
                .build();
    }

    // ===== 1회 예약 이체 (reserveOnceTransferNow) =====

    @Test
    @DisplayName("1회 예약 이체 - 잔액 차감/증가 및 거래내역 기록 정상 동작")
    void reserveOnceTransferNow_success() {
        // given
        TransferRequest request = TransferRequest.create(
                "1111111111", "", "2222222222", BigDecimal.valueOf(100_000));

        when(accountService.getTwoAccountsForUpdate("1111111111", "2222222222"))
                .thenReturn(new Account[]{senderAccount, receiverAccount});

        // when
        transferService.reserveOnceTransferNow(request);

        // then - 잔액 업데이트 확인
        verify(accountService).updateAccountBalanceBoth(
                eq("1111111111"), eq(BigDecimal.valueOf(900_000)),
                eq("2222222222"), eq(BigDecimal.valueOf(600_000))
        );

        // 거래 결과 저장 확인
        ArgumentCaptor<TransferTransactionResult> senderCaptor =
                ArgumentCaptor.forClass(TransferTransactionResult.class);
        ArgumentCaptor<TransferTransactionResult> receiverCaptor =
                ArgumentCaptor.forClass(TransferTransactionResult.class);
        verify(transferTransactionResultService).saveTransferTransactionResultBoth(
                senderCaptor.capture(), receiverCaptor.capture()
        );

        TransferTransactionResult senderResult = senderCaptor.getValue();
        assertThat(senderResult.getTransferTransactionType())
                .isEqualTo(TransferTransactionType.RESERVE_ONCE_OUT);
        assertThat(senderResult.getTransferAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(100_000));
        assertThat(senderResult.getBeforeBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(1_000_000));
        assertThat(senderResult.getAfterBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(900_000));
        assertThat(senderResult.getSelfAccountNumber()).isEqualTo("1111111111");
        assertThat(senderResult.getOtherAccountNumber()).isEqualTo("2222222222");

        TransferTransactionResult receiverResult = receiverCaptor.getValue();
        assertThat(receiverResult.getTransferTransactionType())
                .isEqualTo(TransferTransactionType.RESERVE_ONCE_IN);
        assertThat(receiverResult.getTransferAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(100_000));
        assertThat(receiverResult.getBeforeBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(500_000));
        assertThat(receiverResult.getAfterBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(600_000));
        assertThat(receiverResult.getSelfAccountNumber()).isEqualTo("2222222222");
        assertThat(receiverResult.getOtherAccountNumber()).isEqualTo("1111111111");

        // 원장 기록 확인
        verify(ledgerService).recordForTransfer(
                eq(BigDecimal.valueOf(100_000)),
                anyString(),
                eq(TransferTransactionType.RESERVE_ONCE_OUT),
                eq(TransferTransactionType.RESERVE_ONCE_IN)
        );
    }

    @Test
    @DisplayName("1회 예약 이체 - 동일 계좌 예외")
    void reserveOnceTransferNow_sameAccount_throwsException() {
        // given
        TransferRequest request = TransferRequest.create(
                "1111111111", "", "1111111111", BigDecimal.valueOf(100_000));

        // when & then
        assertThatThrownBy(() -> transferService.reserveOnceTransferNow(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender and Receiver account cannot be the same");
    }

    @Test
    @DisplayName("1회 예약 이체 - 잔액 부족 예외")
    void reserveOnceTransferNow_insufficientBalance_throwsException() {
        // given
        TransferRequest request = TransferRequest.create(
                "1111111111", "", "2222222222", BigDecimal.valueOf(2_000_000));

        when(accountService.getTwoAccountsForUpdate("1111111111", "2222222222"))
                .thenReturn(new Account[]{senderAccount, receiverAccount});

        // when & then
        assertThatThrownBy(() -> transferService.reserveOnceTransferNow(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sender balance not enough");
    }

    // ===== 반복 예약 이체 (reserveRepeatTransferNow) =====

    @Test
    @DisplayName("반복 예약 이체 - 잔액 차감/증가 및 거래내역 기록 정상 동작")
    void reserveRepeatTransferNow_success() {
        // given
        TransferRequest request = TransferRequest.create(
                "1111111111", "", "2222222222", BigDecimal.valueOf(200_000));

        when(accountService.getTwoAccountsForUpdate("1111111111", "2222222222"))
                .thenReturn(new Account[]{senderAccount, receiverAccount});

        // when
        transferService.reserveRepeatTransferNow(request);

        // then - 잔액 업데이트 확인
        verify(accountService).updateAccountBalanceBoth(
                eq("1111111111"), eq(BigDecimal.valueOf(800_000)),
                eq("2222222222"), eq(BigDecimal.valueOf(700_000))
        );

        // 거래 결과 저장 확인
        ArgumentCaptor<TransferTransactionResult> senderCaptor =
                ArgumentCaptor.forClass(TransferTransactionResult.class);
        ArgumentCaptor<TransferTransactionResult> receiverCaptor =
                ArgumentCaptor.forClass(TransferTransactionResult.class);
        verify(transferTransactionResultService).saveTransferTransactionResultBoth(
                senderCaptor.capture(), receiverCaptor.capture()
        );

        TransferTransactionResult senderResult = senderCaptor.getValue();
        assertThat(senderResult.getTransferTransactionType())
                .isEqualTo(TransferTransactionType.RESERVE_REPEAT_OUT);
        assertThat(senderResult.getTransferAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(200_000));

        TransferTransactionResult receiverResult = receiverCaptor.getValue();
        assertThat(receiverResult.getTransferTransactionType())
                .isEqualTo(TransferTransactionType.RESERVE_REPEAT_IN);
        assertThat(receiverResult.getTransferAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(200_000));

        // 원장 기록 확인
        verify(ledgerService).recordForTransfer(
                eq(BigDecimal.valueOf(200_000)),
                anyString(),
                eq(TransferTransactionType.RESERVE_REPEAT_OUT),
                eq(TransferTransactionType.RESERVE_REPEAT_IN)
        );
    }

    @Test
    @DisplayName("반복 예약 이체 - 동일 계좌 예외")
    void reserveRepeatTransferNow_sameAccount_throwsException() {
        // given
        TransferRequest request = TransferRequest.create(
                "1111111111", "", "1111111111", BigDecimal.valueOf(100_000));

        // when & then
        assertThatThrownBy(() -> transferService.reserveRepeatTransferNow(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender and Receiver account cannot be the same");
    }

    @Test
    @DisplayName("반복 예약 이체 - 잔액 부족 예외")
    void reserveRepeatTransferNow_insufficientBalance_throwsException() {
        // given
        TransferRequest request = TransferRequest.create(
                "1111111111", "", "2222222222", BigDecimal.valueOf(5_000_000));

        when(accountService.getTwoAccountsForUpdate("1111111111", "2222222222"))
                .thenReturn(new Account[]{senderAccount, receiverAccount});

        // when & then
        assertThatThrownBy(() -> transferService.reserveRepeatTransferNow(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sender balance not enough");
    }
}
