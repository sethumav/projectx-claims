package ca.on.wsib.digital.projectx.claims.web.rest;

import ca.on.wsib.digital.projectx.claims.ClaimsApp;

import ca.on.wsib.digital.projectx.claims.domain.Authority;
import ca.on.wsib.digital.projectx.claims.domain.Claim;
import ca.on.wsib.digital.projectx.claims.domain.User;
import ca.on.wsib.digital.projectx.claims.repository.ClaimRepository;
import ca.on.wsib.digital.projectx.claims.repository.UserRepository;
import ca.on.wsib.digital.projectx.claims.security.AuthoritiesConstants;
import ca.on.wsib.digital.projectx.claims.web.rest.errors.ExceptionTranslator;

import io.advantageous.boon.core.Sets;
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
 * Test class for the ClaimResource REST controller.
 *
 * @see ClaimResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClaimsApp.class)
public class ClaimResourceIntTest {

    private static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restClaimMockMvc;

    private Claim claim;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ClaimResource claimResource = new ClaimResource(claimRepository);
        this.restClaimMockMvc = MockMvcBuilders.standaloneSetup(claimResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    private User getTestUser() {
        return userRepository.findOne(Long.valueOf(4));
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public Claim createEntity(EntityManager em) {
        Claim claim = new Claim()
            .identifier(DEFAULT_IDENTIFIER);
        // Add required entity
        User user = getTestUser();
        em.persist(user);
        em.flush();
        claim.setUser(user);
        return claim;
    }

    @Before
    public void initTest() {
        claim = createEntity(em);
    }

    @Test
    @Transactional
    public void createClaim() throws Exception {
        int databaseSizeBeforeCreate = claimRepository.findAll().size();

        // Create the Claim
        restClaimMockMvc.perform(post("/api/claims")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(claim)))
            .andExpect(status().isCreated());

        // Validate the Claim in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeCreate + 1);
        Claim testClaim = claimList.get(claimList.size() - 1);
        assertThat(testClaim.getIdentifier()).isEqualTo(DEFAULT_IDENTIFIER);
    }

    @Test
    @Transactional
    public void createClaimWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = claimRepository.findAll().size();

        // Create the Claim with an existing ID
        claim.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restClaimMockMvc.perform(post("/api/claims")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(claim)))
            .andExpect(status().isBadRequest());

        // Validate the Claim in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkIdentifierIsRequired() throws Exception {
        int databaseSizeBeforeTest = claimRepository.findAll().size();
        // set the field null
        claim.setIdentifier(null);

        // Create the Claim, which fails.

        restClaimMockMvc.perform(post("/api/claims")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(claim)))
            .andExpect(status().isBadRequest());

        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClaims() throws Exception {
        // Initialize the database
        claimRepository.saveAndFlush(claim);

        // Get all the claimList
        restClaimMockMvc.perform(get("/api/claims?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(claim.getId().intValue())))
            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER.toString())));
    }

    @Test
    @Transactional
    public void getClaim() throws Exception {
        // Initialize the database
        claimRepository.saveAndFlush(claim);

        // Get the claim
        restClaimMockMvc.perform(get("/api/claims/{id}", claim.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(claim.getId().intValue()))
            .andExpect(jsonPath("$.identifier").value(DEFAULT_IDENTIFIER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingClaim() throws Exception {
        // Get the claim
        restClaimMockMvc.perform(get("/api/claims/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClaim() throws Exception {
        // Initialize the database
        claimRepository.saveAndFlush(claim);
        int databaseSizeBeforeUpdate = claimRepository.findAll().size();

        // Update the claim
        Claim updatedClaim = claimRepository.findOne(claim.getId());
        // Disconnect from session so that the updates on updatedClaim are not directly saved in db
        em.detach(updatedClaim);
        updatedClaim
            .identifier(UPDATED_IDENTIFIER);

        restClaimMockMvc.perform(put("/api/claims")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedClaim)))
            .andExpect(status().isOk());

        // Validate the Claim in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeUpdate);
        Claim testClaim = claimList.get(claimList.size() - 1);
        assertThat(testClaim.getIdentifier()).isEqualTo(UPDATED_IDENTIFIER);
    }

    @Test
    @Transactional
    public void updateNonExistingClaim() throws Exception {
        int databaseSizeBeforeUpdate = claimRepository.findAll().size();

        // Create the Claim

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restClaimMockMvc.perform(put("/api/claims")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(claim)))
            .andExpect(status().isCreated());

        // Validate the Claim in the database
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteClaim() throws Exception {
        // Initialize the database
        claimRepository.saveAndFlush(claim);
        int databaseSizeBeforeDelete = claimRepository.findAll().size();

        // Get the claim
        restClaimMockMvc.perform(delete("/api/claims/{id}", claim.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Claim> claimList = claimRepository.findAll();
        assertThat(claimList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Claim.class);
        Claim claim1 = new Claim();
        claim1.setId(1L);
        Claim claim2 = new Claim();
        claim2.setId(claim1.getId());
        assertThat(claim1).isEqualTo(claim2);
        claim2.setId(2L);
        assertThat(claim1).isNotEqualTo(claim2);
        claim1.setId(null);
        assertThat(claim1).isNotEqualTo(claim2);
    }
}
