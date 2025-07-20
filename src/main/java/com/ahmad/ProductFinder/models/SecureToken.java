package com.ahmad.ProductFinder.models;

import com.ahmad.ProductFinder.enums.TokenType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "secure_tokens")
public class SecureToken {
    private static BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(12);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Column(updatable = false)
    @Basic(optional = false)
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    public SecureToken() {

    }

    public boolean isExpired() {
        return getExpiredAt().isBefore(LocalDateTime.now());
    }

    public SecureToken(User user, TokenType type, LocalDateTime expiredAt, String token) {
        this.token = token;
        this.expiredAt = expiredAt;
        this.user = user;
        this.tokenType = type;
    }

//    public SecureToken() {
//        this.token = token;
//        this.expiredAt = LocalDateTime.now().plusHours(2);
//        this.tokenType = TokenType.PASSWORD_RESET;
//    }
}
