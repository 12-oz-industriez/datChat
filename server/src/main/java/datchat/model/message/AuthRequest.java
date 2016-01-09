package datchat.model.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import datchat.model.annotation.PayloadSubType;
import datchat.model.common.MessageType;

@PayloadSubType(MessageType.AUTH)
public class AuthRequest {
    private final String username;
    private final String password;

    @JsonCreator
    public AuthRequest(@JsonProperty("username") String username,
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
