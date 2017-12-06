package ca.on.wsib.digital.projectx.claims.web.rest;

import com.codahale.metrics.annotation.Timed;
import ca.on.wsib.digital.projectx.claims.domain.Form6;

import ca.on.wsib.digital.projectx.claims.repository.Form6Repository;
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
 * REST controller for managing Form6.
 */
@RestController
@RequestMapping("/api")
public class Form6Resource {

    private final Logger log = LoggerFactory.getLogger(Form6Resource.class);

    private static final String ENTITY_NAME = "form6";

    private final Form6Repository form6Repository;

    public Form6Resource(Form6Repository form6Repository) {
        this.form6Repository = form6Repository;
    }

    /**
     * POST  /form-6-s : Create a new form6.
     *
     * @param form6 the form6 to create
     * @return the ResponseEntity with status 201 (Created) and with body the new form6, or with status 400 (Bad Request) if the form6 has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/form-6-s")
    @Timed
    public ResponseEntity<Form6> createForm6(@Valid @RequestBody Form6 form6) throws URISyntaxException {
        log.debug("REST request to save Form6 : {}", form6);
        if (form6.getId() != null) {
            throw new BadRequestAlertException("A new form6 cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Form6 result = form6Repository.save(form6);
        return ResponseEntity.created(new URI("/api/form-6-s/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /form-6-s : Updates an existing form6.
     *
     * @param form6 the form6 to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated form6,
     * or with status 400 (Bad Request) if the form6 is not valid,
     * or with status 500 (Internal Server Error) if the form6 couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/form-6-s")
    @Timed
    public ResponseEntity<Form6> updateForm6(@Valid @RequestBody Form6 form6) throws URISyntaxException {
        log.debug("REST request to update Form6 : {}", form6);
        if (form6.getId() == null) {
            return createForm6(form6);
        }
        Form6 result = form6Repository.save(form6);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, form6.getId().toString()))
            .body(result);
    }

    /**
     * GET  /form-6-s : get all the form6S.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of form6S in body
     */
    @GetMapping("/form-6-s")
    @Timed
    public ResponseEntity<List<Form6>> getAllForm6S(Pageable pageable) {
        log.debug("REST request to get a page of Form6S");
        Page<Form6> page = form6Repository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/form-6-s");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /form-6-s/:id : get the "id" form6.
     *
     * @param id the id of the form6 to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the form6, or with status 404 (Not Found)
     */
    @GetMapping("/form-6-s/{id}")
    @Timed
    public ResponseEntity<Form6> getForm6(@PathVariable Long id) {
        log.debug("REST request to get Form6 : {}", id);
        Form6 form6 = form6Repository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(form6));
    }

    /**
     * DELETE  /form-6-s/:id : delete the "id" form6.
     *
     * @param id the id of the form6 to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/form-6-s/{id}")
    @Timed
    public ResponseEntity<Void> deleteForm6(@PathVariable Long id) {
        log.debug("REST request to delete Form6 : {}", id);
        form6Repository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
