package com.example.identity.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseEntity implements Serializable {
	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	@Column(name = "created_at")
	Date createdAt;
	@Column(name = "updated_at")
	Date updatedAt;

	@PrePersist()
	void persistCreatedAt() {
		if (this.createdAt != null) {
			return;
		}
		this.createdAt = new Date(System.currentTimeMillis());

	}

	@PreUpdate()
	void persistUpdatedAt() {
		if (this.updatedAt != null) {
			return;
		}
		this.updatedAt = new Date(System.currentTimeMillis());

	}
}
