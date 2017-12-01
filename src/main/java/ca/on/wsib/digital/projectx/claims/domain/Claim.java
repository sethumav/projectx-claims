package ca.on.wsib.digital.projectx.claims.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import ca.on.wsib.digital.projectx.claims.domain.enumeration.ClaimStatus;


/**
 * A Claim.
 */
@Entity
@Table(name = "claim")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Claim implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 6)
    @Column(name = "identifier", nullable = false)
    private String identifier;

    @NotNull
    @Column(name = "openeddt", nullable = false)
    private LocalDate openeddt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status;

    @ManyToOne(optional = false)
    @NotNull
    private Worker worker;

    @ManyToOne
    private Employer employer;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Claim identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LocalDate getOpeneddt() {
        return openeddt;
    }

    public Claim openeddt(LocalDate openeddt) {
        this.openeddt = openeddt;
        return this;
    }

    public void setOpeneddt(LocalDate openeddt) {
        this.openeddt = openeddt;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public Claim status(ClaimStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public Worker getWorker() {
        return worker;
    }

    public Claim worker(Worker worker) {
        this.worker = worker;
        return this;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Employer getEmployer() {
        return employer;
    }

    public Claim employer(Employer employer) {
        this.employer = employer;
        return this;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Claim claim = (Claim) o;
        if (claim.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), claim.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Claim{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", openeddt='" + getOpeneddt() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
