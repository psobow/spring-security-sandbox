package com.sobow.secureweb.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(
    name = "authorities",
    // this replicates: CREATE UNIQUE INDEX ix_auth_user_authority ON authorities (user_id, authority)
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uc_auth_user_authority",
            columnNames = {"user_id", "authority"}
        )
    },
    indexes = {
        @Index(name = "ix_auth_user_authority", columnList = "user_id, authority")
    }
)
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String authority;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public Authority() {
    }
    
    public Authority(String authority, Long id, User user, LocalDateTime createdAt) {
        this.authority = authority;
        this.id = id;
        this.user = user;
        this.createdAt = createdAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public String getAuthority() {
        return authority;
    }
    
    public void setAuthority(String authority) {
        this.authority = authority;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                                   ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                                   : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                                      ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                                      : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Authority authority = (Authority) o;
        return getId() != null && Objects.equals(getId(), authority.getId());
    }
    
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
               ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
               : getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
            "id = " + id + ", " +
            "user = " + user + ", " +
            "authority = " + authority + ", " +
            "createdAt = " + createdAt + ")";
    }
}