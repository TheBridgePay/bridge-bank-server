package bridge.bridge_bank.api.controller;

import bridge.bridge_bank.api.controller.docs.NotificationControllerDocs;
import bridge.bridge_bank.api.dto.TransferNotificationResponse;
import bridge.bridge_bank.domain.notification.TransferNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

    private final TransferNotificationService transferNotificationService;

    @GetMapping("/api/accounts/{accountNumber}/notifications")
    @Override
    public ResponseEntity<Page<TransferNotificationResponse>> getNotifications(
            @PathVariable String accountNumber, Pageable pageable) {
        Page<TransferNotificationResponse> response =
                transferNotificationService.getNotifications(accountNumber, pageable)
                        .map(TransferNotificationResponse::from);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/accounts/{accountNumber}/notifications/unread")
    @Override
    public ResponseEntity<Page<TransferNotificationResponse>> getUnreadNotifications(
            @PathVariable String accountNumber, Pageable pageable) {
        Page<TransferNotificationResponse> response =
                transferNotificationService.getUnreadNotifications(accountNumber, pageable)
                        .map(TransferNotificationResponse::from);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/accounts/{accountNumber}/notifications/unread/count")
    @Override
    public ResponseEntity<Long> getUnreadCount(@PathVariable String accountNumber) {
        long count = transferNotificationService.getUnreadCount(accountNumber);
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/api/accounts/{accountNumber}/notifications/{id}/read")
    @Override
    public ResponseEntity<Void> markAsRead(
            @PathVariable String accountNumber, @PathVariable Long id) {
        transferNotificationService.markAsRead(id, accountNumber);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/accounts/{accountNumber}/notifications/read-all")
    @Override
    public ResponseEntity<Integer> markAllAsRead(@PathVariable String accountNumber) {
        int count = transferNotificationService.markAllAsRead(accountNumber);
        return ResponseEntity.ok(count);
    }
}
