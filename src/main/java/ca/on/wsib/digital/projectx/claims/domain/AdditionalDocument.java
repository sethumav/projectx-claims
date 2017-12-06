package ca.on.wsib.digital.projectx.claims.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import ca.on.wsib.digital.projectx.claims.domain.enumeration.DocumentType;


/**
 * A AdditionalDocument.
 */
@Entity
@Table(name = "additional_document")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AdditionalDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Lob
    @Column(name = "jhi_file", nullable = false)
    private byte[] file;

    @Column(name = "jhi_file_content_type", nullable = false)
    private String fileContentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    private DocumentType imageType;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "additional_document_claim",
               joinColumns = @JoinColumn(name="additional_documents_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="claims_id", referencedColumnName="id"))
    private Set<Claim> claims = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public AdditionalDocument name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public AdditionalDocument description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getFile() {
        return file;
    }

    public AdditionalDocument file(byte[] file) {
        this.file = file;
        return this;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public AdditionalDocument fileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
        return this;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public DocumentType getImageType() {
        return imageType;
    }

    public AdditionalDocument imageType(DocumentType imageType) {
        this.imageType = imageType;
        return this;
    }

    public void setImageType(DocumentType imageType) {
        this.imageType = imageType;
    }

    public Set<Claim> getClaims() {
        return claims;
    }

    public AdditionalDocument claims(Set<Claim> claims) {
        this.claims = claims;
        return this;
    }

    public AdditionalDocument addClaim(Claim claim) {
        this.claims.add(claim);
        claim.getAdditionalDocuments().add(this);
        return this;
    }

    public AdditionalDocument removeClaim(Claim claim) {
        this.claims.remove(claim);
        claim.getAdditionalDocuments().remove(this);
        return this;
    }

    public void setClaims(Set<Claim> claims) {
        this.claims = claims;
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
        AdditionalDocument additionalDocument = (AdditionalDocument) o;
        if (additionalDocument.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), additionalDocument.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AdditionalDocument{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", file='" + getFile() + "'" +
            ", fileContentType='" + getFileContentType() + "'" +
            ", imageType='" + getImageType() + "'" +
            "}";
    }
}
