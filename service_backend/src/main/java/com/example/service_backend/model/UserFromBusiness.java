package com.example.service_backend.model;

import lombok.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class UserFromBusiness {

    private Long id;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private String name;

    @NonNull
    private String phoneNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFromBusiness user = (UserFromBusiness) o;
        return Objects.equals(id, user.id) && username.equals(user.username) && email.equals(user.email) && password.equals(user.password) && name.equals(user.name) && phoneNumber.equals(user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, name, phoneNumber);
    }

}