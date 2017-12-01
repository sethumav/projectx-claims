package ca.on.wsib.digital.projectx.claims.repository;

import ca.on.wsib.digital.projectx.claims.domain.Employer;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Employer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {

}
