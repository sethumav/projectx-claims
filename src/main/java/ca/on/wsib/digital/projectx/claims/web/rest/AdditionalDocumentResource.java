package ca.on.wsib.digital.projectx.claims.web.rest;

import com.codahale.metrics.annotation.Timed;
import ca.on.wsib.digital.projectx.claims.domain.AdditionalDocument;

import ca.on.wsib.digital.projectx.claims.repository.AdditionalDocumentRepository;
import ca.on.wsib.digital.projectx.claims.web.rest.errors.BadRequestAlertException;
import ca.on.wsib.digital.projectx.claims.web.rest.util.HeaderUtil;
import ca.on.wsib.digital.projectx.claims.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing AdditionalDocument.
 */
@RestController
@RequestMapping("/api")
public class AdditionalDocumentResource {

    private final Logger log = LoggerFactory.getLogger(AdditionalDocumentResource.class);

    private static final String ENTITY_NAME = "additionalDocument";

    private final AdditionalDocumentRepository additionalDocumentRepository;

    public AdditionalDocumentResource(AdditionalDocumentRepository additionalDocumentRepository) {
        this.additionalDocumentRepository = additionalDocumentRepository;
    }

    /**
     * POST  /additional-documents : Create a new additionalDocument.
     *
     * @param additionalDocument the additionalDocument to create
     * @return the ResponseEntity with status 201 (Created) and with body the new additionalDocument, or with status 400 (Bad Request) if the additionalDocument has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/additional-documents")
    @Timed
    public ResponseEntity<AdditionalDocument> createAdditionalDocument(@Valid @RequestBody AdditionalDocument additionalDocument) throws URISyntaxException {
        log.debug("REST request to save AdditionalDocument : {}", additionalDocument);
        if (additionalDocument.getId() != null) {
            throw new BadRequestAlertException("A new additionalDocument cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AdditionalDocument result = additionalDocumentRepository.save(additionalDocument);
        return ResponseEntity.created(new URI("/api/additional-documents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /additional-documents : Updates an existing additionalDocument.
     *
     * @param additionalDocument the additionalDocument to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated additionalDocument,
     * or with status 400 (Bad Request) if the additionalDocument is not valid,
     * or with status 500 (Internal Server Error) if the additionalDocument couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/additional-documents")
    @Timed
    public ResponseEntity<AdditionalDocument> updateAdditionalDocument(@Valid @RequestBody AdditionalDocument additionalDocument) throws URISyntaxException {
        log.debug("REST request to update AdditionalDocument : {}", additionalDocument);
        if (additionalDocument.getId() == null) {
            return createAdditionalDocument(additionalDocument);
        }
        AdditionalDocument result = additionalDocumentRepository.save(additionalDocument);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, additionalDocument.getId().toString()))
            .body(result);
    }

    /**
     * GET  /additional-documents : get all the additionalDocuments.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of additionalDocuments in body
     */
    @GetMapping("/additional-documents")
    @Timed
    public ResponseEntity<List<AdditionalDocument>> getAllAdditionalDocuments(Pageable pageable) {
        log.debug("REST request to get a page of AdditionalDocuments");
        Page<AdditionalDocument> page = additionalDocumentRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/additional-documents");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /additional-documents/:id : get the "id" additionalDocument.
     *
     * @param id the id of the additionalDocument to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the additionalDocument, or with status 404 (Not Found)
     */
    @GetMapping("/additional-documents/{id}")
    @Timed
    public ResponseEntity<AdditionalDocument> getAdditionalDocument(@PathVariable Long id) {
        log.debug("REST request to get AdditionalDocument : {}", id);
        AdditionalDocument additionalDocument = additionalDocumentRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(additionalDocument));
    }

    /**
     * DELETE  /additional-documents/:id : delete the "id" additionalDocument.
     *
     * @param id the id of the additionalDocument to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/additional-documents/{id}")
    @Timed
    public ResponseEntity<Void> deleteAdditionalDocument(@PathVariable Long id) {
        log.debug("REST request to delete AdditionalDocument : {}", id);
        additionalDocumentRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
