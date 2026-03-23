package bridge.bridge_bank.api.controller;

import bridge.bridge_bank.domain.transfer.TransferRequest;
import bridge.bridge_bank.domain.transfer.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<Void> transfer(@RequestBody TransferRequest request) {
        transferService.simpleTransferNow(request);
        return ResponseEntity.ok().build();
    }
}
