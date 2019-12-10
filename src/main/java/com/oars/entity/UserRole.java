package com.oars.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.AssociationOverride;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

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

    public UserRole(User user, Role role, Long performedBy) {
        setUser(user);
        setRole(role);
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
