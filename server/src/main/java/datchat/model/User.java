package datchat.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.karneim.pojobuilder.GeneratePojoBuilder;
import org.bson.types.ObjectId;

public class User {
    private final ObjectId id;
    private final String username;
    private final String password;

    @GeneratePojoBuilder
    public User(ObjectId id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("password") String password) {
        this(null, username, password);
    }

    public ObjectId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public UserBuilder toBuilder() {
        return new UserBuilder()
                .withId(id)
                .withUsername(username)
                .withPassword(password);
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }
}
