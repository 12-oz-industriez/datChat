package datchat.model.message.register;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import datchat.model.annotation.PayloadSubType;
import datchat.model.common.RequestMessageType;

@PayloadSubType(RequestMessageType.REGISTER)
public class RegisterRequest {
    private final String username;
    private final String password;

    @JsonCreator
    public RegisterRequest(@JsonProperty("username") String username,
                           @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
