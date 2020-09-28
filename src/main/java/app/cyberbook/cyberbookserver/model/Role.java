package app.cyberbook.cyberbookserver.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_ADMIN, ROLE_CLIENT, ROLE_FEEDBACK_MANAGER;

    public String getAuthority() {
        return name();
    }

    public int getCode() {
        return ordinal();
    }

}