package bridge.bridge_bank.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreateRequest {
    private String memberName;
    private String password;
}
