package bridge.bridge_bank.api.controller;

import bridge.bridge_bank.api.dto.ReserveOnceScheduleResponse;
import bridge.bridge_bank.api.dto.ReserveRepeatScheduleResponse;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.ReserveOnceTransferScheduleService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.dto.ReserveOnceTransferScheduleCreateRequest;
import bridge.bridge_bank.domain.reserve_transfer_schedule.once.dto.ReserveOnceTransferScheduleTargetOption;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.ReserveRepeatTransferScheduleService;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.dto.ReserveRepeatTransferScheduleCreateRequest;
import bridge.bridge_bank.domain.reserve_transfer_schedule.repeat.dto.ReserveRepeatTransferScheduleTargetOption;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReserveTransferScheduleController {

    private final ReserveOnceTransferScheduleService reserveOnceTransferScheduleService;
    private final ReserveRepeatTransferScheduleService reserveRepeatTransferScheduleService;

    // ===== Once =====

    @PostMapping("/reserve-transfers/once")
    public ResponseEntity<Void> createOnceSchedule(
            @RequestBody ReserveOnceTransferScheduleCreateRequest request) {
        reserveOnceTransferScheduleService.createReserveOnceTransferSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/accounts/{accountNumber}/reserve-transfers/once")
    public ResponseEntity<List<ReserveOnceScheduleResponse>> getOnceSchedules(
            @PathVariable String accountNumber,
            @RequestParam(required = false) String receiverAccountNumber) {

        ReserveOnceTransferScheduleTargetOption option = new ReserveOnceTransferScheduleTargetOption();
        option.setReceiverAccountNumber(receiverAccountNumber);

        List<ReserveOnceScheduleResponse> response =
                reserveOnceTransferScheduleService.getReserveOnceTransferSchedules(accountNumber, option)
                        .stream()
                        .map(ReserveOnceScheduleResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reserve-transfers/once/{id}")
    public ResponseEntity<Void> deleteOnceSchedule(@PathVariable Long id) {
        reserveOnceTransferScheduleService.deleteReserveOnceTransferScheduleById(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Repeat =====

    @PostMapping("/reserve-transfers/repeat")
    public ResponseEntity<Void> createRepeatSchedule(
            @RequestBody ReserveRepeatTransferScheduleCreateRequest request) {
        reserveRepeatTransferScheduleService.createReserveRepeatTransferSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/accounts/{accountNumber}/reserve-transfers/repeat")
    public ResponseEntity<List<ReserveRepeatScheduleResponse>> getRepeatSchedules(
            @PathVariable String accountNumber,
            @RequestParam(required = false) String receiverAccountNumber) {

        ReserveRepeatTransferScheduleTargetOption option = new ReserveRepeatTransferScheduleTargetOption();
        option.setReceiverAccountNumber(receiverAccountNumber);

        List<ReserveRepeatScheduleResponse> response =
                reserveRepeatTransferScheduleService.getReserveRepeatTransferSchedules(accountNumber, option)
                        .stream()
                        .map(ReserveRepeatScheduleResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reserve-transfers/repeat/{id}")
    public ResponseEntity<Void> deleteRepeatSchedule(@PathVariable Long id) {
        reserveRepeatTransferScheduleService.deleteReserveRepeatTransferScheduleById(id);
        return ResponseEntity.noContent().build();
    }
}
