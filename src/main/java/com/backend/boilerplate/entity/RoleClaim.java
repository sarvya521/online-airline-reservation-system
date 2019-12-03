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
@Table(name = "role_claim")
@AssociationOverride(name = "id.role",
    joinColumns = @JoinColumn(name = "fk_role_id"))
@AssociationOverride(name = "id.claim",
    joinColumns = @JoinColumn(name = "fk_claim_id"))
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class RoleClaim implements Serializable {
    @Getter
    @Setter
    @EqualsAndHashCode(of = {"role", "claim"})
    @NoArgsConstructor
    @Embeddable
    public static class RoleClaimId implements Serializable {
        @ManyToOne(cascade = CascadeType.ALL)
        private Role role;

        @ManyToOne(cascade = CascadeType.ALL)
        private Claim claim;
    }

    @EmbeddedId
    private RoleClaimId id = new RoleClaimId();

    @Column(name = "performed_by", nullable = false)
    private Long performedBy;

    @Generated(GenerationTime.ALWAYS)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ts", columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP", nullable = false,
        insertable = false, updatable = false)
    private Date timestamp;

    public RoleClaim(Role role, Claim claim, Long performedBy) {
        setRole(role);
        setClaim(claim);
        setPerformedBy(performedBy);
    }

    public void setClaim(Claim claim) {
        this.getId().setClaim(claim);
    }

    public void setRole(Role role) {
        this.getId().setRole(role);
    }

    public Claim getClaim() {
        return this.getId().getClaim();
    }

    public Role getRole() {
        return this.getId().getRole();
    }
}
