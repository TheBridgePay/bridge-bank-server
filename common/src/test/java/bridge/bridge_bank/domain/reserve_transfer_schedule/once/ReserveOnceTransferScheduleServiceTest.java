package bridge.bridge_bank.domain.reserve_transfer_schedule.once;

import bridge.bridge_bank.domain.account.AccountService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.entity.ReserveOnceTransferSchedule;
import bridge.bridge_bank.domain.account.entity.Account;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.dto.ReserveOnceTransferScheduleCreateRequest;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.dto.ReserveOnceTransferScheduleTargetOption;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.repository.ReserveOnceTransferScheduleQueryRepository;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.repository.ReserveOnceTransferScheduleRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveOnceTransferScheduleServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private ReserveOnceTransferScheduleRepository reserveOnceTransferScheduleRepository;

    @Mock
    private ReserveOnceTransferScheduleQueryRepository reserveOnceTransferScheduleQueryRepository;

    @InjectMocks
    private ReserveOnceTransferScheduleService service;

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
    @DisplayName("1회 예약 이체 스케줄 생성 - 정상")
    void createReserveOnceTransferSchedule_success() {
        // given
        ReserveOnceTransferScheduleCreateRequest request = new ReserveOnceTransferScheduleCreateRequest();
        request.setSenderAccount("1111111111");
        request.setSenderPassword("1234");
        request.setReceiverAccount("2222222222");
        request.setTransferAmount(BigDecimal.valueOf(10_000));
        request.setTransferDateTime(LocalDateTime.of(2026, 4, 1, 10, 0));

        when(accountService.getTwoAccounts("1111111111", "2222222222"))
                .thenReturn(new Account[]{senderAccount, receiverAccount});

        // when
        service.createReserveOnceTransferSchedule(request);

        // then
        ArgumentCaptor<ReserveOnceTransferSchedule> captor =
                ArgumentCaptor.forClass(ReserveOnceTransferSchedule.class);
        verify(reserveOnceTransferScheduleRepository).save(captor.capture());

        ReserveOnceTransferSchedule saved = captor.getValue();
        assertThat(saved.getSenderAccountNumber()).isEqualTo("1111111111");
        assertThat(saved.getReceiverAccountNumber()).isEqualTo("2222222222");
        assertThat(saved.getTransferAmount()).isEqualByComparingTo(BigDecimal.valueOf(10_000));
        assertThat(saved.getTransferDateTime()).isEqualTo(LocalDateTime.of(2026, 4, 1, 10, 0));
    }

    @Test
    @DisplayName("1회 예약 이체 스케줄 생성 - 동일 계좌 예외")
    void createReserveOnceTransferSchedule_sameAccount_throwsException() {
        // given
        ReserveOnceTransferScheduleCreateRequest request = new ReserveOnceTransferScheduleCreateRequest();
        request.setSenderAccount("1111111111");
        request.setSenderPassword("1234");
        request.setReceiverAccount("1111111111");
        request.setTransferAmount(BigDecimal.valueOf(10_000));
        request.setTransferDateTime(LocalDateTime.of(2026, 4, 1, 10, 0));

        // when & then
        assertThatThrownBy(() -> service.createReserveOnceTransferSchedule(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender and Receiver account cannot be the same");

        verify(reserveOnceTransferScheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("1회 예약 이체 스케줄 생성 - 비밀번호 불일치 예외")
    void createReserveOnceTransferSchedule_wrongPassword_throwsException() {
        // given
        ReserveOnceTransferScheduleCreateRequest request = new ReserveOnceTransferScheduleCreateRequest();
        request.setSenderAccount("1111111111");
        request.setSenderPassword("wrong_password");
        request.setReceiverAccount("2222222222");
        request.setTransferAmount(BigDecimal.valueOf(10_000));
        request.setTransferDateTime(LocalDateTime.of(2026, 4, 1, 10, 0));

        when(accountService.getTwoAccounts("1111111111", "2222222222"))
                .thenReturn(new Account[]{senderAccount, receiverAccount});

        // when & then
        assertThatThrownBy(() -> service.createReserveOnceTransferSchedule(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sender account password does not match sender account password");

        verify(reserveOnceTransferScheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("대기 중인 1회 예약 이체 스케줄 조회")
    void getPendingReserveOnceTransferSchedules_success() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ReserveOnceTransferSchedule schedule = ReserveOnceTransferSchedule.create(
                "1111111111", "2222222222", BigDecimal.valueOf(10_000), now.minusHours(1)
        );

        when(reserveOnceTransferScheduleQueryRepository.getPendingReserveOnceTransferSchedules(now))
                .thenReturn(List.of(schedule));

        // when
        List<ReserveOnceTransferSchedule> result = service.getPendingReserveOnceTransferSchedules(now);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSenderAccountNumber()).isEqualTo("1111111111");
        assertThat(result.get(0).getReceiverAccountNumber()).isEqualTo("2222222222");
    }

    @Test
    @DisplayName("보내는 계좌 기준 1회 예약 이체 스케줄 조회")
    void getReserveOnceTransferSchedules_withReceiverFilter() {
        // given
        ReserveOnceTransferScheduleTargetOption targetOption = new ReserveOnceTransferScheduleTargetOption();
        targetOption.setReceiverAccountNumber("2222222222");

        ReserveOnceTransferSchedule schedule = ReserveOnceTransferSchedule.create(
                "1111111111", "2222222222", BigDecimal.valueOf(10_000),
                LocalDateTime.of(2026, 4, 1, 10, 0)
        );

        when(reserveOnceTransferScheduleQueryRepository
                .getReserveOnceTransferSchedules("1111111111", targetOption))
                .thenReturn(List.of(schedule));

        // when
        List<ReserveOnceTransferSchedule> result =
                service.getReserveOnceTransferSchedules("1111111111", targetOption);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReceiverAccountNumber()).isEqualTo("2222222222");
    }

    @Test
    @DisplayName("1회 예약 이체 스케줄 ID로 삭제")
    void deleteReserveOnceTransferScheduleById_success() {
        // when
        service.deleteReserveOnceTransferScheduleById(1L);

        // then
        verify(reserveOnceTransferScheduleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("보내는 계좌 기준 1회 예약 이체 스케줄 삭제")
    void deleteReserveOnceTransferSchedules_success() {
        // given
        ReserveOnceTransferScheduleTargetOption targetOption = new ReserveOnceTransferScheduleTargetOption();

        // when
        service.deleteReserveOnceTransferSchedules("1111111111", targetOption);

        // then
        verify(reserveOnceTransferScheduleQueryRepository)
                .deleteReserveOnceTransferSchedule("1111111111", targetOption);
    }
}
