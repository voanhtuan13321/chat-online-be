package com.chat.chat_online_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Abstract base class for auditable entities.
 * <p>
 * This class is marked as MappedSuperclass, meaning it is not an entity itself,
 * but rather a class that can be inherited by other entities.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AuditableEntity {
    @CreatedDate
    @Column(name = "ts_create", nullable = false, updatable = false)
    Instant tsCreate;

    @LastModifiedDate
    @Column(name = "ts_modify")
    Instant tsModify;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    Long createdBy;

    @LastModifiedBy
    @Column(name = "modified_by")
    Long modifiedBy;

    @Version
    private int version;

    @Column(name = "is_deleted", nullable = false)
    boolean isDeleted = false;
}
