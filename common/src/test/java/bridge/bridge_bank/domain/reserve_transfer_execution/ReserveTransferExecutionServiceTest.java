package bridge.bridge_bank.domain.reserve_transfer_execution;

import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferScheduleService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.ReserveRepeatTransferScheduleService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.RepeatType;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import bridge.bridge_bank.domain.transfer.TransferRequest;
import bridge.bridge_bank.domain.transfer.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveTransferExecutionServiceTest {

    @Mock
    private TransferService transferService;

    @Mock
    private ReserveOnceTransferScheduleService reserveOnceTransferScheduleService;

    @Mock
    private ReserveRepeatTransferScheduleService reserveRepeatTransferScheduleService;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private ReserveTransferExecutionService service;

    @BeforeEach
    void setUp() {
        // TransactionTemplate.execute() - 콜백을 즉시 실행하도록 설정
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
    }

    @Test
    @DisplayName("1회 예약 이체 실행 - 성공 시 이체 수행 후 스케줄 삭제")
    void executeReserveOnceTransfer_success() {
        // given
        ReserveOnceTransferSchedule schedule = ReserveOnceTransferSchedule.builder()
                .id(1L)
                .senderAccountNumber("1111111111")
                .receiverAccountNumber("2222222222")
                .transferAmount(BigDecimal.valueOf(10_000))
                .transferDateTime(LocalDateTime.now().minusMinutes(5))
                .build();

        // when
        service.executeReserveOnceTransfer(schedule);

        // then - 이체 수행 확인
        ArgumentCaptor<TransferRequest> transferCaptor =
                ArgumentCaptor.forClass(TransferRequest.class);
        verify(transferService).reserveOnceTransferNow(transferCaptor.capture());

        TransferRequest capturedRequest = transferCaptor.getValue();
        assertThat(capturedRequest.getSenderAccount()).isEqualTo("1111111111");
        assertThat(capturedRequest.getReceiverAccount()).isEqualTo("2222222222");
        assertThat(capturedRequest.getTransferAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(10_000));
        assertThat(capturedRequest.getSenderPassword()).isEmpty();

        // 스케줄 삭제 확인
        verify(reserveOnceTransferScheduleService).deleteReserveOnceTransferScheduleById(1L);
    }

    @Test
    @DisplayName("1회 예약 이체 실행 - 이체 실패해도 스케줄은 삭제")
    void executeReserveOnceTransfer_failure_stillDeletesSchedule() {
        // given
        ReserveOnceTransferSchedule schedule = ReserveOnceTransferSchedule.builder()
                .id(1L)
                .senderAccountNumber("1111111111")
                .receiverAccountNumber("2222222222")
                .transferAmount(BigDecimal.valueOf(10_000))
                .transferDateTime(LocalDateTime.now().minusMinutes(5))
                .build();

        doThrow(new IllegalArgumentException("sender balance not enough"))
                .when(transferService).reserveOnceTransferNow(any());

        // when
        service.executeReserveOnceTransfer(schedule);

        // then - 이체 실패해도 스케줄은 반드시 삭제
        verify(reserveOnceTransferScheduleService).deleteReserveOnceTransferScheduleById(1L);
    }

    @Test
    @DisplayName("반복 예약 이체 실행 - 성공 시 이체 수행 후 스케줄 갱신")
    void executeReserveRepeatTransfer_success() {
        // given
        ReserveRepeatTransferSchedule schedule = ReserveRepeatTransferSchedule.builder()
                .id(1L)
                .senderAccountNumber("1111111111")
                .receiverAccountNumber("2222222222")
                .transferAmount(BigDecimal.valueOf(50_000))
                .transferDateTime(LocalDateTime.now().minusMinutes(5))
                .repeatType(RepeatType.MONTHLY)
                .repeatValue(1)
                .build();

        // when
        service.executeReserveRepeatTransfer(schedule);

        // then - 이체 수행 확인
        ArgumentCaptor<TransferRequest> transferCaptor =
                ArgumentCaptor.forClass(TransferRequest.class);
        verify(transferService).reserveRepeatTransferNow(transferCaptor.capture());

        TransferRequest capturedRequest = transferCaptor.getValue();
        assertThat(capturedRequest.getSenderAccount()).isEqualTo("1111111111");
        assertThat(capturedRequest.getReceiverAccount()).isEqualTo("2222222222");
        assertThat(capturedRequest.getTransferAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(50_000));

        // 스케줄 갱신 확인
        verify(reserveRepeatTransferScheduleService)
                .renewReserveRepeatTransferSchedule(schedule);
    }

    @Test
    @DisplayName("반복 예약 이체 실행 - 이체 실패해도 스케줄은 갱신")
    void executeReserveRepeatTransfer_failure_stillRenewsSchedule() {
        // given
        ReserveRepeatTransferSchedule schedule = ReserveRepeatTransferSchedule.builder()
                .id(1L)
                .senderAccountNumber("1111111111")
                .receiverAccountNumber("2222222222")
                .transferAmount(BigDecimal.valueOf(50_000))
                .transferDateTime(LocalDateTime.now().minusMinutes(5))
                .repeatType(RepeatType.DAILY)
                .repeatValue(1)
                .build();

        doThrow(new IllegalArgumentException("sender balance not enough"))
                .when(transferService).reserveRepeatTransferNow(any());

        // when
        service.executeReserveRepeatTransfer(schedule);

        // then - 이체 실패해도 스케줄은 반드시 갱신 (다음 주기를 위해)
        verify(reserveRepeatTransferScheduleService)
                .renewReserveRepeatTransferSchedule(schedule);
    }
}
