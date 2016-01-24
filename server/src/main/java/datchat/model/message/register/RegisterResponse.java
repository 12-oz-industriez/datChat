package datchat.model.message.register;

import datchat.model.User;

public class RegisterResponse {
    private final User user;

    public RegisterResponse(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
