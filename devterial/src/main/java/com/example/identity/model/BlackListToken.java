/**
 * 
 */
package com.example.identity.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * * @author admin
 * 
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "black_list_token")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BlackListToken  {

	@Id
	@Column(name = "token", unique = true, nullable = false)
	String token;

	@Column(name = "expired_date", nullable = false)
	LocalDateTime expiredDate;

	@Column(name = "create_date", nullable = false)
	@Setter(value = lombok.AccessLevel.NONE)
	private LocalDateTime createAt;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	User user;

	@PrePersist
	public void prePersist() {
		if (this.createAt != null) {
			return;
		}
		this.createAt = LocalDateTime.now();
	}
}
