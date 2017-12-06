package ca.on.wsib.digital.projectx.claims.repository;

import ca.on.wsib.digital.projectx.claims.domain.Form6;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Form6 entity.
 */
@SuppressWarnings("unused")
@Repository
public interface Form6Repository extends JpaRepository<Form6, Long> {

}
