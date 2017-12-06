package ca.on.wsib.digital.projectx.claims.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import ca.on.wsib.digital.projectx.claims.domain.enumeration.Province;


/**
 * A Form6.
 */
@Entity
@Table(name = "form_6")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Form6 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "address")
    private String address;

    @Size(min = 10, max = 10)
    @Pattern(regexp = "[0-9]+")
    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(name = "city_or_town_name")
    private String cityOrTownName;

    @Enumerated(EnumType.STRING)
    @Column(name = "province")
    private Province province;

    @Pattern(regexp = "[a-zA-Z][0-9][a-zA-Z] [0-9][a-zA-Z][0-9]")
    @Column(name = "postal_code")
    private String postalCode;

    @Lob
    @Column(name = "additional_information")
    private String additionalInformation;

    @Column(name = "submitted_date")
    private Instant submittedDate;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Claim claim;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public Form6 lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public Form6 firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAddress() {
        return address;
    }

    public Form6 address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Form6 phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCityOrTownName() {
        return cityOrTownName;
    }

    public Form6 cityOrTownName(String cityOrTownName) {
        this.cityOrTownName = cityOrTownName;
        return this;
    }

    public void setCityOrTownName(String cityOrTownName) {
        this.cityOrTownName = cityOrTownName;
    }

    public Province getProvince() {
        return province;
    }

    public Form6 province(Province province) {
        this.province = province;
        return this;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Form6 postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public Form6 additionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public Instant getSubmittedDate() {
        return submittedDate;
    }

    public Form6 submittedDate(Instant submittedDate) {
        this.submittedDate = submittedDate;
        return this;
    }

    public void setSubmittedDate(Instant submittedDate) {
        this.submittedDate = submittedDate;
    }

    public Claim getClaim() {
        return claim;
    }

    public Form6 claim(Claim claim) {
        this.claim = claim;
        return this;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
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
        Form6 form6 = (Form6) o;
        if (form6.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), form6.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Form6{" +
            "id=" + getId() +
            ", lastName='" + getLastName() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", address='" + getAddress() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", cityOrTownName='" + getCityOrTownName() + "'" +
            ", province='" + getProvince() + "'" +
            ", postalCode='" + getPostalCode() + "'" +
            ", additionalInformation='" + getAdditionalInformation() + "'" +
            ", submittedDate='" + getSubmittedDate() + "'" +
            "}";
    }
}
