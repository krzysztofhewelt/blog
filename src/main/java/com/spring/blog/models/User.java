package com.spring.blog.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name="users")
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private Date registeredAt;
    private Date lastLoginAt;
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;
    private String authority;

    @OneToMany(mappedBy="authorId", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    private List<Comment> comments = new ArrayList<>();

    public User(Long id, String username, String email, String password, Date registeredAt, Date lastLoginAt, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, String authority) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.registeredAt = registeredAt;
        this.lastLoginAt = lastLoginAt;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authority = authority;
    }

    public User() {}

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}
