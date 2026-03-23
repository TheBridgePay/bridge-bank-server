package bridge.bridge_bank.api.controller;

import bridge.bridge_bank.api.controller.docs.TransferControllerDocs;
import bridge.bridge_bank.domain.transfer.TransferRequest;
import bridge.bridge_bank.domain.transfer.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController implements TransferControllerDocs {

    private final TransferService transferService;

    @PostMapping
    @Override
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request) {
        transferService.simpleTransferNow(request);
        return ResponseEntity.ok().build();
    }
}
