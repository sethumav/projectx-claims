package ca.on.wsib.digital.projectx.claims.web.rest;

import com.codahale.metrics.annotation.Timed;
import ca.on.wsib.digital.projectx.claims.domain.Claim;

import ca.on.wsib.digital.projectx.claims.repository.ClaimRepository;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for managing Claim.
 */
@RestController
@RequestMapping("/api")
public class ClaimResource {

    private final Logger log = LoggerFactory.getLogger(ClaimResource.class);

    private static final String ENTITY_NAME = "claim";

    private final ClaimRepository claimRepository;

    public ClaimResource(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    /**
     * POST  /claims : Create a new claim.
     *
     * @param claim the claim to create
     * @return the ResponseEntity with status 201 (Created) and with body the new claim, or with status 400 (Bad Request) if the claim has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/claims")
    @Timed
    public ResponseEntity<Claim> createClaim(@Valid @RequestBody Claim claim) throws URISyntaxException {
        log.debug("REST request to save Claim : {}", claim);
        if (claim.getId() != null) {
            throw new BadRequestAlertException("A new claim cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Claim result = claimRepository.save(claim);
        return ResponseEntity.created(new URI("/api/claims/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /claims : Updates an existing claim.
     *
     * @param claim the claim to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated claim,
     * or with status 400 (Bad Request) if the claim is not valid,
     * or with status 500 (Internal Server Error) if the claim couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/claims")
    @Timed
    public ResponseEntity<Claim> updateClaim(@Valid @RequestBody Claim claim) throws URISyntaxException {

        //comment for demo
        log.debug("REST request to update Claim : {}", claim);
        if (claim.getId() == null) {
            return createClaim(claim);
        }
        Claim result = claimRepository.save(claim);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, claim.getId().toString()))
            .body(result);
    }

    /**
     * GET  /claims : get all the claims.
     *
     * @param pageable the pagination information
     * @param filter the filter of the request
     * @return the ResponseEntity with status 200 (OK) and the list of claims in body
     */
    @GetMapping("/claims")
    @Timed
    public ResponseEntity<List<Claim>> getAllClaims(Pageable pageable, @RequestParam(required = false) String filter) {
        if ("form6-is-null".equals(filter)) {
            log.debug("REST request to get all Claims where form6 is null");
            return new ResponseEntity<>(StreamSupport
                .stream(claimRepository.findAll().spliterator(), false)
                .filter(claim -> claim.getForm6() == null)
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        log.debug("REST request to get a page of Claims");
        Page<Claim> page = claimRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/claims");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /claims/:id : get the "id" claim.
     *
     * @param id the id of the claim to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the claim, or with status 404 (Not Found)
     */
    @GetMapping("/claims/{id}")
    @Timed
    public ResponseEntity<Claim> getClaim(@PathVariable Long id) {
        log.debug("REST request to get Claim : {}", id);
        Claim claim = claimRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(claim));
    }

    /**
     * DELETE  /claims/:id : delete the "id" claim.
     *
     * @param id the id of the claim to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/claims/{id}")
    @Timed
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        log.debug("REST request to delete Claim : {}", id);
        claimRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
