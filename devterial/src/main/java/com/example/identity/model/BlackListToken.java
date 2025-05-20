/**
 *
 */
package com.example.identity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * * @author admin
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "black_list_token")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BlackListToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "token", unique = true, nullable = false, columnDefinition = "varchar(512)")
    String token;
    @Column(name = "expired_date", nullable = false)
    @JsonProperty("expired_date")
    LocalDateTime expiredDate;

    @Column(name = "create_date", nullable = false)
    @JsonProperty("create_at")
    LocalDateTime createAt;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    User user;

    @PrePersist
    public void prePersist() {
        if (this.createAt != null) {
            return;
        }
        this.createAt = LocalDateTime.now();
    }
}
