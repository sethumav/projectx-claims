package ca.on.wsib.digital.projectx.claims.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


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

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    @OneToOne(mappedBy = "claim")
    @JsonIgnore
    private Form6 form6;

    @ManyToMany(mappedBy = "claims")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<AdditionalDocument> additionalDocuments = new HashSet<>();

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

    public User getUser() {
        return user;
    }

    public Claim user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Form6 getForm6() {
        return form6;
    }

    public Claim form6(Form6 form6) {
        this.form6 = form6;
        return this;
    }

    public void setForm6(Form6 form6) {
        this.form6 = form6;
    }

    public Set<AdditionalDocument> getAdditionalDocuments() {
        return additionalDocuments;
    }

    public Claim additionalDocuments(Set<AdditionalDocument> additionalDocuments) {
        this.additionalDocuments = additionalDocuments;
        return this;
    }

    public Claim addAdditionalDocument(AdditionalDocument additionalDocument) {
        this.additionalDocuments.add(additionalDocument);
        additionalDocument.getClaims().add(this);
        return this;
    }

    public Claim removeAdditionalDocument(AdditionalDocument additionalDocument) {
        this.additionalDocuments.remove(additionalDocument);
        additionalDocument.getClaims().remove(this);
        return this;
    }

    public void setAdditionalDocuments(Set<AdditionalDocument> additionalDocuments) {
        this.additionalDocuments = additionalDocuments;
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
            "}";
    }
}
