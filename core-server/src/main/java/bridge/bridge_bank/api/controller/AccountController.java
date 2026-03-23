package bridge.bridge_bank.api.controller;

import bridge.bridge_bank.api.controller.docs.AccountControllerDocs;
import bridge.bridge_bank.api.dto.AccountCreateRequest;
import bridge.bridge_bank.api.dto.AccountResponse;
import bridge.bridge_bank.domain.account.AccountService;
import bridge.bridge_bank.domain.account.entity.Account;
import bridge.bridge_bank.global.error.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController implements AccountControllerDocs {

    private final AccountService accountService;

    @PostMapping
    @Override
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        Account account = Account.create(request.getMemberName(), request.getPassword());
        accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountResponse.from(account));
    }

    @GetMapping("/{accountNumber}")
    @Override
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        Account account = accountService.getAccount(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("계좌를 찾을 수 없습니다: " + accountNumber));
        return ResponseEntity.ok(AccountResponse.from(account));
    }
}
