package ca.on.wsib.digital.projectx.claims.web.rest;

import ca.on.wsib.digital.projectx.claims.ClaimsApp;

import ca.on.wsib.digital.projectx.claims.domain.Employer;
import ca.on.wsib.digital.projectx.claims.repository.EmployerRepository;
import ca.on.wsib.digital.projectx.claims.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static ca.on.wsib.digital.projectx.claims.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EmployerResource REST controller.
 *
 * @see EmployerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClaimsApp.class)
public class EmployerResourceIntTest {

    private static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restEmployerMockMvc;

    private Employer employer;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EmployerResource employerResource = new EmployerResource(employerRepository);
        this.restEmployerMockMvc = MockMvcBuilders.standaloneSetup(employerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employer createEntity(EntityManager em) {
        Employer employer = new Employer()
            .identifier(DEFAULT_IDENTIFIER)
            .name(DEFAULT_NAME);
        return employer;
    }

    @Before
    public void initTest() {
        employer = createEntity(em);
    }

    @Test
    @Transactional
    public void createEmployer() throws Exception {
        int databaseSizeBeforeCreate = employerRepository.findAll().size();

        // Create the Employer
        restEmployerMockMvc.perform(post("/api/employers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employer)))
            .andExpect(status().isCreated());

        // Validate the Employer in the database
        List<Employer> employerList = employerRepository.findAll();
        assertThat(employerList).hasSize(databaseSizeBeforeCreate + 1);
        Employer testEmployer = employerList.get(employerList.size() - 1);
        assertThat(testEmployer.getIdentifier()).isEqualTo(DEFAULT_IDENTIFIER);
        assertThat(testEmployer.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createEmployerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = employerRepository.findAll().size();

        // Create the Employer with an existing ID
        employer.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployerMockMvc.perform(post("/api/employers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employer)))
            .andExpect(status().isBadRequest());

        // Validate the Employer in the database
        List<Employer> employerList = employerRepository.findAll();
        assertThat(employerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkIdentifierIsRequired() throws Exception {
        int databaseSizeBeforeTest = employerRepository.findAll().size();
        // set the field null
        employer.setIdentifier(null);

        // Create the Employer, which fails.

        restEmployerMockMvc.perform(post("/api/employers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employer)))
            .andExpect(status().isBadRequest());

        List<Employer> employerList = employerRepository.findAll();
        assertThat(employerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = employerRepository.findAll().size();
        // set the field null
        employer.setName(null);

        // Create the Employer, which fails.

        restEmployerMockMvc.perform(post("/api/employers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employer)))
            .andExpect(status().isBadRequest());

        List<Employer> employerList = employerRepository.findAll();
        assertThat(employerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEmployers() throws Exception {
        // Initialize the database
        employerRepository.saveAndFlush(employer);

        // Get all the employerList
        restEmployerMockMvc.perform(get("/api/employers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employer.getId().intValue())))
            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getEmployer() throws Exception {
        // Initialize the database
        employerRepository.saveAndFlush(employer);

        // Get the employer
        restEmployerMockMvc.perform(get("/api/employers/{id}", employer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(employer.getId().intValue()))
            .andExpect(jsonPath("$.identifier").value(DEFAULT_IDENTIFIER.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEmployer() throws Exception {
        // Get the employer
        restEmployerMockMvc.perform(get("/api/employers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEmployer() throws Exception {
        // Initialize the database
        employerRepository.saveAndFlush(employer);
        int databaseSizeBeforeUpdate = employerRepository.findAll().size();

        // Update the employer
        Employer updatedEmployer = employerRepository.findOne(employer.getId());
        // Disconnect from session so that the updates on updatedEmployer are not directly saved in db
        em.detach(updatedEmployer);
        updatedEmployer
            .identifier(UPDATED_IDENTIFIER)
            .name(UPDATED_NAME);

        restEmployerMockMvc.perform(put("/api/employers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedEmployer)))
            .andExpect(status().isOk());

        // Validate the Employer in the database
        List<Employer> employerList = employerRepository.findAll();
        assertThat(employerList).hasSize(databaseSizeBeforeUpdate);
        Employer testEmployer = employerList.get(employerList.size() - 1);
        assertThat(testEmployer.getIdentifier()).isEqualTo(UPDATED_IDENTIFIER);
        assertThat(testEmployer.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingEmployer() throws Exception {
        int databaseSizeBeforeUpdate = employerRepository.findAll().size();

        // Create the Employer

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restEmployerMockMvc.perform(put("/api/employers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employer)))
            .andExpect(status().isCreated());

        // Validate the Employer in the database
        List<Employer> employerList = employerRepository.findAll();
        assertThat(employerList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteEmployer() throws Exception {
        // Initialize the database
        employerRepository.saveAndFlush(employer);
        int databaseSizeBeforeDelete = employerRepository.findAll().size();

        // Get the employer
        restEmployerMockMvc.perform(delete("/api/employers/{id}", employer.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Employer> employerList = employerRepository.findAll();
        assertThat(employerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employer.class);
        Employer employer1 = new Employer();
        employer1.setId(1L);
        Employer employer2 = new Employer();
        employer2.setId(employer1.getId());
        assertThat(employer1).isEqualTo(employer2);
        employer2.setId(2L);
        assertThat(employer1).isNotEqualTo(employer2);
        employer1.setId(null);
        assertThat(employer1).isNotEqualTo(employer2);
    }
}
