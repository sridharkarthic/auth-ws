package com.saninnovationslab.authws.userdetail;

public class UserDetailUpdateModel {

    private final UserRole role;
    private final UserStatus status;

    public UserDetailUpdateModel(UserRole role, UserStatus status) {
        this.role = role;
        this.status = status;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

}
