package com.example.backend.model;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import org.springframework.security.core.userdetails.User;

public class AuthenticatedUser extends User {
    private Account account;

    public AuthenticatedUser(Account account, Collection<? extends GrantedAuthority> authorities) {
        super(account.getEmail(), account.getPassword(), authorities);
        this.account = account;
    }

    public AuthenticatedUser(Account account, boolean enabled, boolean accountNonExpired,
    boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(account.getEmail(), account.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.account = account;
    }

    public Account getAccount(){ 
        return account; 
    }
}
