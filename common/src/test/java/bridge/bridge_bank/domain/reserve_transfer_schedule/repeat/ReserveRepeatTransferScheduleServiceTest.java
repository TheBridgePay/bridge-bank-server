package bridge.bridge_bank.domain.reserve_transfer_schedule.repeat;

import bridge.bridge_bank.domain.account.AccountService;
import bridge.bridge_bank.domain.account.entity.Account;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.dto.ReserveRepeatTransferScheduleCreateRequest;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.dto.ReserveRepeatTransferScheduleTargetOption;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.RepeatType;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.entity.ReserveRepeatTransferSchedule;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository.ReserveRepeatTransferScheduleQueryRepository;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.repository.ReserveRepeatTransferScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveRepeatTransferScheduleServiceTest {

    @Mock
    private ReserveRepeatTransferScheduleRepository reserveRepeatTransferScheduleRepository;

    @Mock
    private ReserveRepeatTransferScheduleQueryRepository reserveRepeatTransferScheduleQueryRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private ReserveRepeatTransferScheduleService service;

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

    @Test
    @DisplayName("반복 예약 이체 스케줄 생성 - 정상 (MONTHLY)")
    void createReserveRepeatTransferSchedule_success() {
        // given
        ReserveRepeatTransferScheduleCreateRequest request = new ReserveRepeatTransferScheduleCreateRequest();
        request.setSenderAccount("1111111111");
        request.setSenderPassword("1234");
        request.setReceiverAccount("2222222222");
        request.setTransferAmount(BigDecimal.valueOf(50_000));
        request.setTransferDateTime(LocalDateTime.of(2026, 4, 1, 10, 0));
        request.setRepeatType(RepeatType.MONTHLY);
        request.setRepeatValue(1);

        when(accountService.getTwoAccounts("1111111111", "2222222222"))
                .thenReturn(new Account[]{senderAccount, receiverAccount});

        // when
        service.createReserveRepeatTransferSchedule(request);

        // then
        ArgumentCaptor<ReserveRepeatTransferSchedule> captor =
                ArgumentCaptor.forClass(ReserveRepeatTransferSchedule.class);
        verify(reserveRepeatTransferScheduleRepository).save(captor.capture());

        ReserveRepeatTransferSchedule saved = captor.getValue();
        assertThat(saved.getSenderAccountNumber()).isEqualTo("1111111111");
        assertThat(saved.getReceiverAccountNumber()).isEqualTo("2222222222");
        assertThat(saved.getTransferAmount()).isEqualByComparingTo(BigDecimal.valueOf(50_000));
        assertThat(saved.getRepeatType()).isEqualTo(RepeatType.MONTHLY);
        assertThat(saved.getRepeatValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("반복 예약 이체 스케줄 생성 - 동일 계좌 예외")
    void createReserveRepeatTransferSchedule_sameAccount_throwsException() {
        // given
        ReserveRepeatTransferScheduleCreateRequest request = new ReserveRepeatTransferScheduleCreateRequest();
        request.setSenderAccount("1111111111");
        request.setSenderPassword("1234");
        request.setReceiverAccount("1111111111");
        request.setTransferAmount(BigDecimal.valueOf(50_000));
        request.setTransferDateTime(LocalDateTime.of(2026, 4, 1, 10, 0));
        request.setRepeatType(RepeatType.MONTHLY);
        request.setRepeatValue(1);

        // when & then
        assertThatThrownBy(() -> service.createReserveRepeatTransferSchedule(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender and Receiver account cannot be the same");

        verify(reserveRepeatTransferScheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("반복 예약 이체 스케줄 생성 - 비밀번호 불일치 예외")
    void createReserveRepeatTransferSchedule_wrongPassword_throwsException() {
        // given
        ReserveRepeatTransferScheduleCreateRequest request = new ReserveRepeatTransferScheduleCreateRequest();
        request.setSenderAccount("1111111111");
        request.setSenderPassword("wrong_password");
        request.setReceiverAccount("2222222222");
        request.setTransferAmount(BigDecimal.valueOf(50_000));
        request.setTransferDateTime(LocalDateTime.of(2026, 4, 1, 10, 0));
        request.setRepeatType(RepeatType.MONTHLY);
        request.setRepeatValue(1);

        when(accountService.getTwoAccounts("1111111111", "2222222222"))
                .thenReturn(new Account[]{senderAccount, receiverAccount});

        // when & then
        assertThatThrownBy(() -> service.createReserveRepeatTransferSchedule(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender account password does not match sender account password");

        verify(reserveRepeatTransferScheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("반복 예약 이체 갱신 - DAILY (1일 간격)")
    void renewReserveRepeatTransferSchedule_daily() {
        // given
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(3);
        ReserveRepeatTransferSchedule schedule = ReserveRepeatTransferSchedule.builder()
                .id(1L)
                .senderAccountNumber("1111111111")
                .receiverAccountNumber("2222222222")
                .transferAmount(BigDecimal.valueOf(10_000))
                .transferDateTime(pastDateTime)
                .repeatType(RepeatType.DAILY)
                .repeatValue(1)
                .build();

        // when
        service.renewReserveRepeatTransferSchedule(schedule);

        // then
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(reserveRepeatTransferScheduleRepository)
                .updateTransferDateTimeById(eq(1L), dateCaptor.capture());

        LocalDateTime newDateTime = dateCaptor.getValue();
        assertThat(newDateTime).isAfter(LocalDateTime.now().minusMinutes(1));
        assertThat(newDateTime).isBefore(LocalDateTime.now().plusDays(1).plusMinutes(1));
    }

    @Test
    @DisplayName("반복 예약 이체 갱신 - WEEKLY (1주 간격)")
    void renewReserveRepeatTransferSchedule_weekly() {
        // given
        LocalDateTime pastDateTime = LocalDateTime.now().minusWeeks(3);
        ReserveRepeatTransferSchedule schedule = ReserveRepeatTransferSchedule.builder()
                .id(2L)
                .senderAccountNumber("1111111111")
                .receiverAccountNumber("2222222222")
                .transferAmount(BigDecimal.valueOf(10_000))
                .transferDateTime(pastDateTime)
                .repeatType(RepeatType.WEEKLY)
                .repeatValue(1)
                .build();

        // when
        service.renewReserveRepeatTransferSchedule(schedule);

        // then
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(reserveRepeatTransferScheduleRepository)
                .updateTransferDateTimeById(eq(2L), dateCaptor.capture());

        LocalDateTime newDateTime = dateCaptor.getValue();
        assertThat(newDateTime).isAfter(LocalDateTime.now().minusMinutes(1));
        assertThat(newDateTime).isBefore(LocalDateTime.now().plusWeeks(1).plusMinutes(1));
    }

    @Test
    @DisplayName("반복 예약 이체 갱신 - MONTHLY (1개월 간격)")
    void renewReserveRepeatTransferSchedule_monthly() {
        // given
        LocalDateTime pastDateTime = LocalDateTime.now().minusMonths(3);
        ReserveRepeatTransferSchedule schedule = ReserveRepeatTransferSchedule.builder()
                .id(3L)
                .senderAccountNumber("1111111111")
                .receiverAccountNumber("2222222222")
                .transferAmount(BigDecimal.valueOf(10_000))
                .transferDateTime(pastDateTime)
                .repeatType(RepeatType.MONTHLY)
                .repeatValue(1)
                .build();

        // when
        service.renewReserveRepeatTransferSchedule(schedule);

        // then
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(reserveRepeatTransferScheduleRepository)
                .updateTransferDateTimeById(eq(3L), dateCaptor.capture());

        LocalDateTime newDateTime = dateCaptor.getValue();
        assertThat(newDateTime).isAfter(LocalDateTime.now().minusMinutes(1));
        assertThat(newDateTime).isBefore(LocalDateTime.now().plusMonths(1).plusMinutes(1));
    }

    @Test
    @DisplayName("반복 예약 이체 갱신 - HOURLY (2시간 간격)")
    void renewReserveRepeatTransferSchedule_hourly() {
        // given
        LocalDateTime pastDateTime = LocalDateTime.now().minusHours(5);
        ReserveRepeatTransferSchedule schedule = ReserveRepeatTransferSchedule.builder()
                .id(4L)
                .senderAccountNumber("1111111111")
                .receiverAccountNumber("2222222222")
                .transferAmount(BigDecimal.valueOf(10_000))
                .transferDateTime(pastDateTime)
                .repeatType(RepeatType.HOURLY)
                .repeatValue(2)
                .build();

        // when
        service.renewReserveRepeatTransferSchedule(schedule);

        // then
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(reserveRepeatTransferScheduleRepository)
                .updateTransferDateTimeById(eq(4L), dateCaptor.capture());

        LocalDateTime newDateTime = dateCaptor.getValue();
        assertThat(newDateTime).isAfter(LocalDateTime.now().minusMinutes(1));
        assertThat(newDateTime).isBefore(LocalDateTime.now().plusHours(2).plusMinutes(1));
    }

    @Test
    @DisplayName("반복 예약 이체 갱신 - DAILY repeatValue=3, 여러 주기 건너뛰기")
    void renewReserveRepeatTransferSchedule_daily_multipleSkips() {
        // given: 10일 전 기준, 3일 간격 -> 4번 반복하여 now+2일 부근
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(10);
        ReserveRepeatTransferSchedule schedule = ReserveRepeatTransferSchedule.builder()
                .id(5L)
                .senderAccountNumber("1111111111")
                .receiverAccountNumber("2222222222")
                .transferAmount(BigDecimal.valueOf(10_000))
                .transferDateTime(pastDateTime)
                .repeatType(RepeatType.DAILY)
                .repeatValue(3)
                .build();

        // when
        service.renewReserveRepeatTransferSchedule(schedule);

        // then
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(reserveRepeatTransferScheduleRepository)
                .updateTransferDateTimeById(eq(5L), dateCaptor.capture());

        LocalDateTime newDateTime = dateCaptor.getValue();
        // now - 10 + 3*4 = now + 2, 대략 now+1일 ~ now+3일 사이
        assertThat(newDateTime).isAfter(LocalDateTime.now().plusDays(1));
        assertThat(newDateTime).isBefore(LocalDateTime.now().plusDays(3).plusMinutes(1));
    }

    @Test
    @DisplayName("대기 중인 반복 예약 이체 스케줄 조회")
    void getPendingReserveRepeatTransferSchedules_success() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ReserveRepeatTransferSchedule schedule = ReserveRepeatTransferSchedule.create(
                "1111111111", "2222222222", BigDecimal.valueOf(10_000),
                now.minusHours(1), RepeatType.DAILY, 1
        );

        when(reserveRepeatTransferScheduleQueryRepository.getPendingReserveRepeatTransferSchedules(now))
                .thenReturn(List.of(schedule));

        // when
        List<ReserveRepeatTransferSchedule> result = service.getPendingReserveRepeatTransferSchedules(now);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRepeatType()).isEqualTo(RepeatType.DAILY);
    }

    @Test
    @DisplayName("반복 예약 이체 스케줄 ID로 삭제")
    void deleteReserveRepeatTransferScheduleById_success() {
        // when
        service.deleteReserveRepeatTransferScheduleById(1L);

        // then
        verify(reserveRepeatTransferScheduleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("보내는 계좌 기준 반복 예약 이체 스케줄 삭제")
    void deleteReserveRepeatTransferSchedules_success() {
        // given
        ReserveRepeatTransferScheduleTargetOption targetOption =
                new ReserveRepeatTransferScheduleTargetOption();

        // when
        service.deleteReserveRepeatTransferSchedules("1111111111", targetOption);

        // then
        verify(reserveRepeatTransferScheduleQueryRepository)
                .deleteReserveRepeatTransferSchedule("1111111111", targetOption);
    }
}
