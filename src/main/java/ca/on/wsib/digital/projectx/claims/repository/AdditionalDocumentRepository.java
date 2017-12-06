package ca.on.wsib.digital.projectx.claims.repository;

import ca.on.wsib.digital.projectx.claims.domain.AdditionalDocument;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the AdditionalDocument entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdditionalDocumentRepository extends JpaRepository<AdditionalDocument, Long> {
    @Query("select distinct additional_document from AdditionalDocument additional_document left join fetch additional_document.claims")
    List<AdditionalDocument> findAllWithEagerRelationships();

    @Query("select additional_document from AdditionalDocument additional_document left join fetch additional_document.claims where additional_document.id =:id")
    AdditionalDocument findOneWithEagerRelationships(@Param("id") Long id);

}
