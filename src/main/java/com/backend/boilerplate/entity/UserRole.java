package com.backend.boilerplate.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.AssociationOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author sarvesh
 * @version 0.0.2
 * @since 0.0.1
 */
@Entity
@Table(name = "user_role")
@AssociationOverride(name = "id.user",
    joinColumns = @JoinColumn(name = "fk_user_id"))
@AssociationOverride(name = "id.role",
    joinColumns = @JoinColumn(name = "fk_role_id"))
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class UserRole implements Serializable {
    @Getter
    @Setter
    @EqualsAndHashCode(of = {"user", "role"})
    @NoArgsConstructor
    @Embeddable
    public static class UserRoleId implements Serializable {
        @ManyToOne(cascade = CascadeType.ALL)
        private User user;

        @ManyToOne(cascade = CascadeType.ALL)
        private Role role;
    }

    @EmbeddedId
    private UserRoleId id = new UserRoleId();

    @Column(name = "performed_by", nullable = false)
    private Long performedBy;

    @Generated(GenerationTime.ALWAYS)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ts", columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP", nullable = false,
        insertable = false, updatable = false)
    private Date timestamp;

    public UserRole(User user, Role role, Long performedBy) {
        setUser(user);
        setRole(role);
        setPerformedBy(performedBy);
    }

    public void setUser(User user) {
        this.getId().setUser(user);
    }

    public void setRole(Role role) {
        this.getId().setRole(role);
    }

    public User getUser() {
        return this.getId().getUser();
    }

    public Role getRole() {
        return this.getId().getRole();
    }
}
