package ca.on.wsib.digital.projectx.claims.web.rest;

import com.codahale.metrics.annotation.Timed;
import ca.on.wsib.digital.projectx.claims.domain.Employer;

import ca.on.wsib.digital.projectx.claims.repository.EmployerRepository;
import ca.on.wsib.digital.projectx.claims.web.rest.errors.BadRequestAlertException;
import ca.on.wsib.digital.projectx.claims.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Employer.
 */
@RestController
@RequestMapping("/api")
public class EmployerResource {

    private final Logger log = LoggerFactory.getLogger(EmployerResource.class);

    private static final String ENTITY_NAME = "employer";

    private final EmployerRepository employerRepository;

    public EmployerResource(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    /**
     * POST  /employers : Create a new employer.
     *
     * @param employer the employer to create
     * @return the ResponseEntity with status 201 (Created) and with body the new employer, or with status 400 (Bad Request) if the employer has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/employers")
    @Timed
    public ResponseEntity<Employer> createEmployer(@Valid @RequestBody Employer employer) throws URISyntaxException {
        log.debug("REST request to save Employer : {}", employer);
        if (employer.getId() != null) {
            throw new BadRequestAlertException("A new employer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Employer result = employerRepository.save(employer);
        return ResponseEntity.created(new URI("/api/employers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /employers : Updates an existing employer.
     *
     * @param employer the employer to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated employer,
     * or with status 400 (Bad Request) if the employer is not valid,
     * or with status 500 (Internal Server Error) if the employer couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/employers")
    @Timed
    public ResponseEntity<Employer> updateEmployer(@Valid @RequestBody Employer employer) throws URISyntaxException {
        log.debug("REST request to update Employer : {}", employer);
        if (employer.getId() == null) {
            return createEmployer(employer);
        }
        Employer result = employerRepository.save(employer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, employer.getId().toString()))
            .body(result);
    }

    /**
     * GET  /employers : get all the employers.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of employers in body
     */
    @GetMapping("/employers")
    @Timed
    public List<Employer> getAllEmployers() {
        log.debug("REST request to get all Employers");
        return employerRepository.findAll();
        }

    /**
     * GET  /employers/:id : get the "id" employer.
     *
     * @param id the id of the employer to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employer, or with status 404 (Not Found)
     */
    @GetMapping("/employers/{id}")
    @Timed
    public ResponseEntity<Employer> getEmployer(@PathVariable Long id) {
        log.debug("REST request to get Employer : {}", id);
        Employer employer = employerRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(employer));
    }

    /**
     * DELETE  /employers/:id : delete the "id" employer.
     *
     * @param id the id of the employer to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/employers/{id}")
    @Timed
    public ResponseEntity<Void> deleteEmployer(@PathVariable Long id) {
        log.debug("REST request to delete Employer : {}", id);
        employerRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
