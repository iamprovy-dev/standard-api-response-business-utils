package zw.saas.validation.responses.configs;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseClass implements Serializable {

    @CreatedDate
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)  // Prevent setter generation
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Setter(AccessLevel.NONE)  // Prevent setter generation
    private LocalDateTime modifiedAt;

    @CreatedBy
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)  // Prevent setter generation
    private UUID createdBy;

    @Column(name = "deleted")
    private boolean deleted = false;

    @Version
    @Column(name = "version")
    @Setter(AccessLevel.NONE)  // Prevent setter generation
    private Long version;

    public void softDelete() { this.deleted = true; }

    public void restore() { this.deleted = false; }
}