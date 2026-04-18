package bridge.bridge_bank.api.controller;

import bridge.bridge_bank.domain.legacy_transfer.LegacyTransferService;
import bridge.bridge_bank.domain.transfer.dto.TransferRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/legacy/transfers")
@RequiredArgsConstructor
public class LegacyTransferController {

    private final LegacyTransferService legacyTransferService;

    @PostMapping
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request) {
        legacyTransferService.simpleTransferNow(request);
        return ResponseEntity.ok().build();
    }
}
