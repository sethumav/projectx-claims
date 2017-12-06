package ca.on.wsib.digital.projectx.claims.web.rest;

import ca.on.wsib.digital.projectx.claims.ClaimsApp;

import ca.on.wsib.digital.projectx.claims.domain.Form6;
import ca.on.wsib.digital.projectx.claims.domain.Claim;
import ca.on.wsib.digital.projectx.claims.domain.User;
import ca.on.wsib.digital.projectx.claims.repository.Form6Repository;
import ca.on.wsib.digital.projectx.claims.repository.UserRepository;
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
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ca.on.wsib.digital.projectx.claims.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ca.on.wsib.digital.projectx.claims.domain.enumeration.Province;
/**
 * Test class for the Form6Resource REST controller.
 *
 * @see Form6Resource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClaimsApp.class)
public class Form6ResourceIntTest {

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "4160000000";
    private static final String UPDATED_PHONE_NUMBER = "6479999999";

    private static final String DEFAULT_CITY_OR_TOWN_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CITY_OR_TOWN_NAME = "BBBBBBBBBB";

    private static final Province DEFAULT_PROVINCE = Province.ONTARIO;
    private static final Province UPDATED_PROVINCE = Province.QUEBEC;

    private static final String DEFAULT_POSTAL_CODE = "U2r 2D2";
    private static final String UPDATED_POSTAL_CODE = "E3r 1K3";

    private static final String DEFAULT_ADDITIONAL_INFORMATION = "AAAAAAAAAA";
    private static final String UPDATED_ADDITIONAL_INFORMATION = "BBBBBBBBBB";

    private static final Instant DEFAULT_SUBMITTED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMITTED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private Form6Repository form6Repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restForm6MockMvc;

    private Form6 form6;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final Form6Resource form6Resource = new Form6Resource(form6Repository);
        this.restForm6MockMvc = MockMvcBuilders.standaloneSetup(form6Resource)
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
    public Form6 createEntity(EntityManager em) {
        Form6 form6 = new Form6()
            .lastName(DEFAULT_LAST_NAME)
            .firstName(DEFAULT_FIRST_NAME)
            .address(DEFAULT_ADDRESS)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .cityOrTownName(DEFAULT_CITY_OR_TOWN_NAME)
            .province(DEFAULT_PROVINCE)
            .postalCode(DEFAULT_POSTAL_CODE)
            .additionalInformation(DEFAULT_ADDITIONAL_INFORMATION)
            .submittedDate(DEFAULT_SUBMITTED_DATE);
        // Add required entity
        Claim claim = createClaim(em);
        em.persist(claim);
        em.flush();
        form6.setClaim(claim);
        return form6;
    }

    @Before
    public void initTest() {
        form6 = createEntity(em);
    }

    @Test
    @Transactional
    public void createForm6() throws Exception {
        int databaseSizeBeforeCreate = form6Repository.findAll().size();

        // Create the Form6
        restForm6MockMvc.perform(post("/api/form-6-s")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(form6)))
            .andExpect(status().isCreated());

        // Validate the Form6 in the database
        List<Form6> form6List = form6Repository.findAll();
        assertThat(form6List).hasSize(databaseSizeBeforeCreate + 1);
        Form6 testForm6 = form6List.get(form6List.size() - 1);
        assertThat(testForm6.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testForm6.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testForm6.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testForm6.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testForm6.getCityOrTownName()).isEqualTo(DEFAULT_CITY_OR_TOWN_NAME);
        assertThat(testForm6.getProvince()).isEqualTo(DEFAULT_PROVINCE);
        assertThat(testForm6.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testForm6.getAdditionalInformation()).isEqualTo(DEFAULT_ADDITIONAL_INFORMATION);
        assertThat(testForm6.getSubmittedDate()).isEqualTo(DEFAULT_SUBMITTED_DATE);
    }

    @Test
    @Transactional
    public void createForm6WithExistingId() throws Exception {
        int databaseSizeBeforeCreate = form6Repository.findAll().size();

        // Create the Form6 with an existing ID
        form6.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restForm6MockMvc.perform(post("/api/form-6-s")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(form6)))
            .andExpect(status().isBadRequest());

        // Validate the Form6 in the database
        List<Form6> form6List = form6Repository.findAll();
        assertThat(form6List).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllForm6S() throws Exception {
        // Initialize the database
        form6Repository.saveAndFlush(form6);

        // Get all the form6List
        restForm6MockMvc.perform(get("/api/form-6-s?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(form6.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].cityOrTownName").value(hasItem(DEFAULT_CITY_OR_TOWN_NAME.toString())))
            .andExpect(jsonPath("$.[*].province").value(hasItem(DEFAULT_PROVINCE.toString())))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE.toString())))
            .andExpect(jsonPath("$.[*].additionalInformation").value(hasItem(DEFAULT_ADDITIONAL_INFORMATION.toString())))
            .andExpect(jsonPath("$.[*].submittedDate").value(hasItem(DEFAULT_SUBMITTED_DATE.toString())));
    }

    @Test
    @Transactional
    public void getForm6() throws Exception {
        // Initialize the database
        form6Repository.saveAndFlush(form6);

        // Get the form6
        restForm6MockMvc.perform(get("/api/form-6-s/{id}", form6.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(form6.getId().intValue()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER.toString()))
            .andExpect(jsonPath("$.cityOrTownName").value(DEFAULT_CITY_OR_TOWN_NAME.toString()))
            .andExpect(jsonPath("$.province").value(DEFAULT_PROVINCE.toString()))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE.toString()))
            .andExpect(jsonPath("$.additionalInformation").value(DEFAULT_ADDITIONAL_INFORMATION.toString()))
            .andExpect(jsonPath("$.submittedDate").value(DEFAULT_SUBMITTED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingForm6() throws Exception {
        // Get the form6
        restForm6MockMvc.perform(get("/api/form-6-s/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateForm6() throws Exception {
        // Initialize the database
        form6Repository.saveAndFlush(form6);
        int databaseSizeBeforeUpdate = form6Repository.findAll().size();

        // Update the form6
        Form6 updatedForm6 = form6Repository.findOne(form6.getId());
        // Disconnect from session so that the updates on updatedForm6 are not directly saved in db
        em.detach(updatedForm6);
        updatedForm6
            .lastName(UPDATED_LAST_NAME)
            .firstName(UPDATED_FIRST_NAME)
            .address(UPDATED_ADDRESS)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .cityOrTownName(UPDATED_CITY_OR_TOWN_NAME)
            .province(UPDATED_PROVINCE)
            .postalCode(UPDATED_POSTAL_CODE)
            .additionalInformation(UPDATED_ADDITIONAL_INFORMATION)
            .submittedDate(UPDATED_SUBMITTED_DATE);

        restForm6MockMvc.perform(put("/api/form-6-s")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedForm6)))
            .andExpect(status().isOk());

        // Validate the Form6 in the database
        List<Form6> form6List = form6Repository.findAll();
        assertThat(form6List).hasSize(databaseSizeBeforeUpdate);
        Form6 testForm6 = form6List.get(form6List.size() - 1);
        assertThat(testForm6.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testForm6.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testForm6.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testForm6.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testForm6.getCityOrTownName()).isEqualTo(UPDATED_CITY_OR_TOWN_NAME);
        assertThat(testForm6.getProvince()).isEqualTo(UPDATED_PROVINCE);
        assertThat(testForm6.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testForm6.getAdditionalInformation()).isEqualTo(UPDATED_ADDITIONAL_INFORMATION);
        assertThat(testForm6.getSubmittedDate()).isEqualTo(UPDATED_SUBMITTED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingForm6() throws Exception {
        int databaseSizeBeforeUpdate = form6Repository.findAll().size();

        // Create the Form6

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restForm6MockMvc.perform(put("/api/form-6-s")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(form6)))
            .andExpect(status().isCreated());

        // Validate the Form6 in the database
        List<Form6> form6List = form6Repository.findAll();
        assertThat(form6List).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteForm6() throws Exception {
        // Initialize the database
        form6Repository.saveAndFlush(form6);
        int databaseSizeBeforeDelete = form6Repository.findAll().size();

        // Get the form6
        restForm6MockMvc.perform(delete("/api/form-6-s/{id}", form6.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Form6> form6List = form6Repository.findAll();
        assertThat(form6List).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Form6.class);
        Form6 form61 = new Form6();
        form61.setId(1L);
        Form6 form62 = new Form6();
        form62.setId(form61.getId());
        assertThat(form61).isEqualTo(form62);
        form62.setId(2L);
        assertThat(form61).isNotEqualTo(form62);
        form61.setId(null);
        assertThat(form61).isNotEqualTo(form62);
    }

    public Claim createClaim(EntityManager em) {
        Claim claim = new Claim()
            .identifier("AAAAAAAAAA");
        // Add required entity
        User user = getTestUser();
        em.persist(user);
        em.flush();
        claim.setUser(user);
        return claim;
    }


    private User getTestUser() {
        return userRepository.findOne(Long.valueOf(4));
    }
}
