package com.example.neetflex.patterns.builder;

import com.example.neetflex.model.user.User;
import lombok.Getter;

@Getter
public class UserBuilder implements IBuilder<User> {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;

    public UserBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public User build() {
        User user = new User();
        if (id != null) {
            user.setId(id);
        }
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}