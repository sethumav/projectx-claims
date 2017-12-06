package ca.on.wsib.digital.projectx.claims.web.rest;

import ca.on.wsib.digital.projectx.claims.ClaimsApp;

import ca.on.wsib.digital.projectx.claims.domain.AdditionalDocument;
import ca.on.wsib.digital.projectx.claims.repository.AdditionalDocumentRepository;
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
import java.util.List;

import static ca.on.wsib.digital.projectx.claims.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ca.on.wsib.digital.projectx.claims.domain.enumeration.DocumentType;
/**
 * Test class for the AdditionalDocumentResource REST controller.
 *
 * @see AdditionalDocumentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClaimsApp.class)
public class AdditionalDocumentResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    private static final DocumentType DEFAULT_IMAGE_TYPE = DocumentType.PASSPORT;
    private static final DocumentType UPDATED_IMAGE_TYPE = DocumentType.DIVORCE_ORDER;

    @Autowired
    private AdditionalDocumentRepository additionalDocumentRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAdditionalDocumentMockMvc;

    private AdditionalDocument additionalDocument;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AdditionalDocumentResource additionalDocumentResource = new AdditionalDocumentResource(additionalDocumentRepository);
        this.restAdditionalDocumentMockMvc = MockMvcBuilders.standaloneSetup(additionalDocumentResource)
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
    public static AdditionalDocument createEntity(EntityManager em) {
        AdditionalDocument additionalDocument = new AdditionalDocument()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .file(DEFAULT_FILE)
            .fileContentType(DEFAULT_FILE_CONTENT_TYPE)
            .imageType(DEFAULT_IMAGE_TYPE);
        return additionalDocument;
    }

    @Before
    public void initTest() {
        additionalDocument = createEntity(em);
    }

    @Test
    @Transactional
    public void createAdditionalDocument() throws Exception {
        int databaseSizeBeforeCreate = additionalDocumentRepository.findAll().size();

        // Create the AdditionalDocument
        restAdditionalDocumentMockMvc.perform(post("/api/additional-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(additionalDocument)))
            .andExpect(status().isCreated());

        // Validate the AdditionalDocument in the database
        List<AdditionalDocument> additionalDocumentList = additionalDocumentRepository.findAll();
        assertThat(additionalDocumentList).hasSize(databaseSizeBeforeCreate + 1);
        AdditionalDocument testAdditionalDocument = additionalDocumentList.get(additionalDocumentList.size() - 1);
        assertThat(testAdditionalDocument.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAdditionalDocument.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAdditionalDocument.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testAdditionalDocument.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);
        assertThat(testAdditionalDocument.getImageType()).isEqualTo(DEFAULT_IMAGE_TYPE);
    }

    @Test
    @Transactional
    public void createAdditionalDocumentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = additionalDocumentRepository.findAll().size();

        // Create the AdditionalDocument with an existing ID
        additionalDocument.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdditionalDocumentMockMvc.perform(post("/api/additional-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(additionalDocument)))
            .andExpect(status().isBadRequest());

        // Validate the AdditionalDocument in the database
        List<AdditionalDocument> additionalDocumentList = additionalDocumentRepository.findAll();
        assertThat(additionalDocumentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = additionalDocumentRepository.findAll().size();
        // set the field null
        additionalDocument.setName(null);

        // Create the AdditionalDocument, which fails.

        restAdditionalDocumentMockMvc.perform(post("/api/additional-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(additionalDocument)))
            .andExpect(status().isBadRequest());

        List<AdditionalDocument> additionalDocumentList = additionalDocumentRepository.findAll();
        assertThat(additionalDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFileIsRequired() throws Exception {
        int databaseSizeBeforeTest = additionalDocumentRepository.findAll().size();
        // set the field null
        additionalDocument.setFile(null);

        // Create the AdditionalDocument, which fails.

        restAdditionalDocumentMockMvc.perform(post("/api/additional-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(additionalDocument)))
            .andExpect(status().isBadRequest());

        List<AdditionalDocument> additionalDocumentList = additionalDocumentRepository.findAll();
        assertThat(additionalDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkImageTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = additionalDocumentRepository.findAll().size();
        // set the field null
        additionalDocument.setImageType(null);

        // Create the AdditionalDocument, which fails.

        restAdditionalDocumentMockMvc.perform(post("/api/additional-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(additionalDocument)))
            .andExpect(status().isBadRequest());

        List<AdditionalDocument> additionalDocumentList = additionalDocumentRepository.findAll();
        assertThat(additionalDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAdditionalDocuments() throws Exception {
        // Initialize the database
        additionalDocumentRepository.saveAndFlush(additionalDocument);

        // Get all the additionalDocumentList
        restAdditionalDocumentMockMvc.perform(get("/api/additional-documents?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(additionalDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))))
            .andExpect(jsonPath("$.[*].imageType").value(hasItem(DEFAULT_IMAGE_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getAdditionalDocument() throws Exception {
        // Initialize the database
        additionalDocumentRepository.saveAndFlush(additionalDocument);

        // Get the additionalDocument
        restAdditionalDocumentMockMvc.perform(get("/api/additional-documents/{id}", additionalDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(additionalDocument.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64Utils.encodeToString(DEFAULT_FILE)))
            .andExpect(jsonPath("$.imageType").value(DEFAULT_IMAGE_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAdditionalDocument() throws Exception {
        // Get the additionalDocument
        restAdditionalDocumentMockMvc.perform(get("/api/additional-documents/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAdditionalDocument() throws Exception {
        // Initialize the database
        additionalDocumentRepository.saveAndFlush(additionalDocument);
        int databaseSizeBeforeUpdate = additionalDocumentRepository.findAll().size();

        // Update the additionalDocument
        AdditionalDocument updatedAdditionalDocument = additionalDocumentRepository.findOne(additionalDocument.getId());
        // Disconnect from session so that the updates on updatedAdditionalDocument are not directly saved in db
        em.detach(updatedAdditionalDocument);
        updatedAdditionalDocument
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .imageType(UPDATED_IMAGE_TYPE);

        restAdditionalDocumentMockMvc.perform(put("/api/additional-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAdditionalDocument)))
            .andExpect(status().isOk());

        // Validate the AdditionalDocument in the database
        List<AdditionalDocument> additionalDocumentList = additionalDocumentRepository.findAll();
        assertThat(additionalDocumentList).hasSize(databaseSizeBeforeUpdate);
        AdditionalDocument testAdditionalDocument = additionalDocumentList.get(additionalDocumentList.size() - 1);
        assertThat(testAdditionalDocument.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAdditionalDocument.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAdditionalDocument.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testAdditionalDocument.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
        assertThat(testAdditionalDocument.getImageType()).isEqualTo(UPDATED_IMAGE_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingAdditionalDocument() throws Exception {
        int databaseSizeBeforeUpdate = additionalDocumentRepository.findAll().size();

        // Create the AdditionalDocument

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAdditionalDocumentMockMvc.perform(put("/api/additional-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(additionalDocument)))
            .andExpect(status().isCreated());

        // Validate the AdditionalDocument in the database
        List<AdditionalDocument> additionalDocumentList = additionalDocumentRepository.findAll();
        assertThat(additionalDocumentList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAdditionalDocument() throws Exception {
        // Initialize the database
        additionalDocumentRepository.saveAndFlush(additionalDocument);
        int databaseSizeBeforeDelete = additionalDocumentRepository.findAll().size();

        // Get the additionalDocument
        restAdditionalDocumentMockMvc.perform(delete("/api/additional-documents/{id}", additionalDocument.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AdditionalDocument> additionalDocumentList = additionalDocumentRepository.findAll();
        assertThat(additionalDocumentList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AdditionalDocument.class);
        AdditionalDocument additionalDocument1 = new AdditionalDocument();
        additionalDocument1.setId(1L);
        AdditionalDocument additionalDocument2 = new AdditionalDocument();
        additionalDocument2.setId(additionalDocument1.getId());
        assertThat(additionalDocument1).isEqualTo(additionalDocument2);
        additionalDocument2.setId(2L);
        assertThat(additionalDocument1).isNotEqualTo(additionalDocument2);
        additionalDocument1.setId(null);
        assertThat(additionalDocument1).isNotEqualTo(additionalDocument2);
    }
}
