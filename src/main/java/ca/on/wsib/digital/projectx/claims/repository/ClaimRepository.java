package ca.on.wsib.digital.projectx.claims.repository;

import ca.on.wsib.digital.projectx.claims.domain.Claim;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Claim entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

}
