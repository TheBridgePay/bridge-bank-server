package bridge.bridge_bank.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreateRequest {
    @NotBlank(message = "memberName is required")
    private String memberName;

    @NotBlank(message = "password is required")
    private String password;
}
