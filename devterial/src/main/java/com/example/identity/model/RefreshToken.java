package com.example.identity.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Table(name = "refresh_tokens", indexes = {
        @Index(columnList = "user_id", name = "idx_refresh_token"),
        @Index(columnList = "expired_date", name = "idx_refresh_token")
})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RefreshToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "refresh_token", unique = true, nullable = false, columnDefinition = "varchar(300)")
    String refreshToken;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "created_at")
    @Setter(AccessLevel.NONE)
    Date createdAt;

    @Column(name = "expired_date")
    Date expiredDate;

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = new Date(System.currentTimeMillis());
    }

}
