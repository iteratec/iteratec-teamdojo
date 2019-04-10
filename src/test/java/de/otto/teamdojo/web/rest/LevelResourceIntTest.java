package de.otto.teamdojo.web.rest;

import de.otto.teamdojo.TeamdojoApp;
import de.otto.teamdojo.domain.*;
import de.otto.teamdojo.repository.LevelRepository;
import de.otto.teamdojo.service.LevelQueryService;
import de.otto.teamdojo.service.LevelService;
import de.otto.teamdojo.service.LevelSkillService;
import de.otto.teamdojo.service.dto.LevelDTO;
import de.otto.teamdojo.service.mapper.LevelMapper;
import de.otto.teamdojo.web.rest.errors.ExceptionTranslator;
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
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;


import static de.otto.teamdojo.test.util.BadgeTestDataProvider.alwaysUpToDate;
import static de.otto.teamdojo.test.util.BadgeTestDataProvider.awsReady;
import static de.otto.teamdojo.test.util.DimensionTestDataProvider.operations;
import static de.otto.teamdojo.test.util.DimensionTestDataProvider.security;
import static de.otto.teamdojo.test.util.LevelTestDataProvider.*;
import static de.otto.teamdojo.test.util.SkillTestDataProvider.*;
import static de.otto.teamdojo.test.util.TeamTestDataProvider.ft1;
import static de.otto.teamdojo.test.util.TeamTestDataProvider.ft2;
import static de.otto.teamdojo.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LevelResource REST controller.
 *
 * @see LevelResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TeamdojoApp.class)
public class LevelResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_REQUIRED_SCORE = 0D;
    private static final Double UPDATED_REQUIRED_SCORE = 1D;

    private static final Double DEFAULT_INSTANT_MULTIPLIER = 0D;
    private static final Double UPDATED_INSTANT_MULTIPLIER = 1D;

    private static final Integer DEFAULT_COMPLETION_BONUS = 0;
    private static final Integer UPDATED_COMPLETION_BONUS = 1;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private LevelService levelService;

    @Autowired
    private LevelQueryService levelQueryService;

    @Autowired
    private LevelSkillService levelSkillService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restLevelMockMvc;

    private Level level;

    private Badge badge;

    private Team team1;
    private Team team2;
    private Skill inputValidation;
    private Skill softwareUpdates;
    private Skill strongPasswords;
    private Skill dockerized;
    private Level yellow;
    private Level orange;
    private Level os1;
    private Dimension security;
    private Dimension operations;
    private TeamSkill teamSkill;
    private Badge awsReady;
    private Badge alwaysUpToDate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LevelResource levelResource = new LevelResource(levelService, levelQueryService, levelSkillService);
        this.restLevelMockMvc = MockMvcBuilders.standaloneSetup(levelResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Level createEntity(EntityManager em) {
        Level level = new Level()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .requiredScore(DEFAULT_REQUIRED_SCORE)
            .instantMultiplier(DEFAULT_INSTANT_MULTIPLIER)
            .completionBonus(DEFAULT_COMPLETION_BONUS);
        // Add required entity
        Dimension dimension = DimensionResourceIntTest.createEntity(em);
        em.persist(dimension);
        em.flush();
        level.setDimension(dimension);
        return level;
    }

    @Before
    public void initTest() {
        level = createEntity(em);
    }

    @Test
    @Transactional
    public void createLevel() throws Exception {
        int databaseSizeBeforeCreate = levelRepository.findAll().size();

        // Create the Level
        LevelDTO levelDTO = levelMapper.toDto(level);
        restLevelMockMvc.perform(post("/api/levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isCreated());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeCreate + 1);
        Level testLevel = levelList.get(levelList.size() - 1);
        assertThat(testLevel.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLevel.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testLevel.getRequiredScore()).isEqualTo(DEFAULT_REQUIRED_SCORE);
        assertThat(testLevel.getInstantMultiplier()).isEqualTo(DEFAULT_INSTANT_MULTIPLIER);
        assertThat(testLevel.getCompletionBonus()).isEqualTo(DEFAULT_COMPLETION_BONUS);
    }

    @Test
    @Transactional
    public void createLevelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = levelRepository.findAll().size();

        // Create the Level with an existing ID
        level.setId(1L);
        LevelDTO levelDTO = levelMapper.toDto(level);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLevelMockMvc.perform(post("/api/levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = levelRepository.findAll().size();
        // set the field null
        level.setName(null);

        // Create the Level, which fails.
        LevelDTO levelDTO = levelMapper.toDto(level);

        restLevelMockMvc.perform(post("/api/levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isBadRequest());

        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRequiredScoreIsRequired() throws Exception {
        int databaseSizeBeforeTest = levelRepository.findAll().size();
        // set the field null
        level.setRequiredScore(null);

        // Create the Level, which fails.
        LevelDTO levelDTO = levelMapper.toDto(level);

        restLevelMockMvc.perform(post("/api/levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isBadRequest());

        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkInstantMultiplierIsRequired() throws Exception {
        int databaseSizeBeforeTest = levelRepository.findAll().size();
        // set the field null
        level.setInstantMultiplier(null);

        // Create the Level, which fails.
        LevelDTO levelDTO = levelMapper.toDto(level);

        restLevelMockMvc.perform(post("/api/levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isBadRequest());

        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLevels() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList
        restLevelMockMvc.perform(get("/api/levels?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(level.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].requiredScore").value(hasItem(DEFAULT_REQUIRED_SCORE.doubleValue())))
            .andExpect(jsonPath("$.[*].instantMultiplier").value(hasItem(DEFAULT_INSTANT_MULTIPLIER.doubleValue())))
            .andExpect(jsonPath("$.[*].completionBonus").value(hasItem(DEFAULT_COMPLETION_BONUS)));
    }

    @Test
    @Transactional
    public void getLevel() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get the level
        restLevelMockMvc.perform(get("/api/levels/{id}", level.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(level.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.requiredScore").value(DEFAULT_REQUIRED_SCORE.doubleValue()))
            .andExpect(jsonPath("$.instantMultiplier").value(DEFAULT_INSTANT_MULTIPLIER.doubleValue()))
            .andExpect(jsonPath("$.completionBonus").value(DEFAULT_COMPLETION_BONUS));
    }

    @Test
    @Transactional
    public void getAllLevelsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where name equals to DEFAULT_NAME
        defaultLevelShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the levelList where name equals to UPDATED_NAME
        defaultLevelShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLevelsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where name in DEFAULT_NAME or UPDATED_NAME
        defaultLevelShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the levelList where name equals to UPDATED_NAME
        defaultLevelShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLevelsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where name is not null
        defaultLevelShouldBeFound("name.specified=true");

        // Get all the levelList where name is null
        defaultLevelShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllLevelsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where description equals to DEFAULT_DESCRIPTION
        defaultLevelShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the levelList where description equals to UPDATED_DESCRIPTION
        defaultLevelShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllLevelsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultLevelShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the levelList where description equals to UPDATED_DESCRIPTION
        defaultLevelShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllLevelsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where description is not null
        defaultLevelShouldBeFound("description.specified=true");

        // Get all the levelList where description is null
        defaultLevelShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllLevelsByRequiredScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where requiredScore equals to DEFAULT_REQUIRED_SCORE
        defaultLevelShouldBeFound("requiredScore.equals=" + DEFAULT_REQUIRED_SCORE);

        // Get all the levelList where requiredScore equals to UPDATED_REQUIRED_SCORE
        defaultLevelShouldNotBeFound("requiredScore.equals=" + UPDATED_REQUIRED_SCORE);
    }

    @Test
    @Transactional
    public void getAllLevelsByRequiredScoreIsInShouldWork() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where requiredScore in DEFAULT_REQUIRED_SCORE or UPDATED_REQUIRED_SCORE
        defaultLevelShouldBeFound("requiredScore.in=" + DEFAULT_REQUIRED_SCORE + "," + UPDATED_REQUIRED_SCORE);

        // Get all the levelList where requiredScore equals to UPDATED_REQUIRED_SCORE
        defaultLevelShouldNotBeFound("requiredScore.in=" + UPDATED_REQUIRED_SCORE);
    }

    @Test
    @Transactional
    public void getAllLevelsByRequiredScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where requiredScore is not null
        defaultLevelShouldBeFound("requiredScore.specified=true");

        // Get all the levelList where requiredScore is null
        defaultLevelShouldNotBeFound("requiredScore.specified=false");
    }

    @Test
    @Transactional
    public void getAllLevelsByInstantMultiplierIsEqualToSomething() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where instantMultiplier equals to DEFAULT_INSTANT_MULTIPLIER
        defaultLevelShouldBeFound("instantMultiplier.equals=" + DEFAULT_INSTANT_MULTIPLIER);

        // Get all the levelList where instantMultiplier equals to UPDATED_INSTANT_MULTIPLIER
        defaultLevelShouldNotBeFound("instantMultiplier.equals=" + UPDATED_INSTANT_MULTIPLIER);
    }

    @Test
    @Transactional
    public void getAllLevelsByInstantMultiplierIsInShouldWork() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where instantMultiplier in DEFAULT_INSTANT_MULTIPLIER or UPDATED_INSTANT_MULTIPLIER
        defaultLevelShouldBeFound("instantMultiplier.in=" + DEFAULT_INSTANT_MULTIPLIER + "," + UPDATED_INSTANT_MULTIPLIER);

        // Get all the levelList where instantMultiplier equals to UPDATED_INSTANT_MULTIPLIER
        defaultLevelShouldNotBeFound("instantMultiplier.in=" + UPDATED_INSTANT_MULTIPLIER);
    }

    @Test
    @Transactional
    public void getAllLevelsByInstantMultiplierIsNullOrNotNull() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where instantMultiplier is not null
        defaultLevelShouldBeFound("instantMultiplier.specified=true");

        // Get all the levelList where instantMultiplier is null
        defaultLevelShouldNotBeFound("instantMultiplier.specified=false");
    }

    @Test
    @Transactional
    public void getAllLevelsByCompletionBonusIsEqualToSomething() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where completionBonus equals to DEFAULT_COMPLETION_BONUS
        defaultLevelShouldBeFound("completionBonus.equals=" + DEFAULT_COMPLETION_BONUS);

        // Get all the levelList where completionBonus equals to UPDATED_COMPLETION_BONUS
        defaultLevelShouldNotBeFound("completionBonus.equals=" + UPDATED_COMPLETION_BONUS);
    }

    @Test
    @Transactional
    public void getAllLevelsByCompletionBonusIsInShouldWork() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where completionBonus in DEFAULT_COMPLETION_BONUS or UPDATED_COMPLETION_BONUS
        defaultLevelShouldBeFound("completionBonus.in=" + DEFAULT_COMPLETION_BONUS + "," + UPDATED_COMPLETION_BONUS);

        // Get all the levelList where completionBonus equals to UPDATED_COMPLETION_BONUS
        defaultLevelShouldNotBeFound("completionBonus.in=" + UPDATED_COMPLETION_BONUS);
    }

    @Test
    @Transactional
    public void getAllLevelsByCompletionBonusIsNullOrNotNull() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where completionBonus is not null
        defaultLevelShouldBeFound("completionBonus.specified=true");

        // Get all the levelList where completionBonus is null
        defaultLevelShouldNotBeFound("completionBonus.specified=false");
    }

    @Test
    @Transactional
    public void getAllLevelsByCompletionBonusIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where completionBonus greater than or equals to DEFAULT_COMPLETION_BONUS
        defaultLevelShouldBeFound("completionBonus.greaterOrEqualThan=" + DEFAULT_COMPLETION_BONUS);

        // Get all the levelList where completionBonus greater than or equals to UPDATED_COMPLETION_BONUS
        defaultLevelShouldNotBeFound("completionBonus.greaterOrEqualThan=" + UPDATED_COMPLETION_BONUS);
    }

    @Test
    @Transactional
    public void getAllLevelsByCompletionBonusIsLessThanSomething() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        // Get all the levelList where completionBonus less than or equals to DEFAULT_COMPLETION_BONUS
        defaultLevelShouldNotBeFound("completionBonus.lessThan=" + DEFAULT_COMPLETION_BONUS);

        // Get all the levelList where completionBonus less than or equals to UPDATED_COMPLETION_BONUS
        defaultLevelShouldBeFound("completionBonus.lessThan=" + UPDATED_COMPLETION_BONUS);
    }


    @Test
    @Transactional
    public void getAllLevelsByDimensionIsEqualToSomething() throws Exception {
        // Initialize the database
        Dimension dimension = DimensionResourceIntTest.createEntity(em);
        em.persist(dimension);
        em.flush();
        level.setDimension(dimension);
        levelRepository.saveAndFlush(level);
        Long dimensionId = dimension.getId();

        // Get all the levelList where dimension equals to dimensionId
        defaultLevelShouldBeFound("dimensionId.equals=" + dimensionId);

        // Get all the levelList where dimension equals to dimensionId + 1
        defaultLevelShouldNotBeFound("dimensionId.equals=" + (dimensionId + 1));
    }


    @Test
    @Transactional
    public void getAllLevelsByDependsOnIsEqualToSomething() throws Exception {
        // Initialize the database
        Level dependsOn = LevelResourceIntTest.createEntity(em);
        em.persist(dependsOn);
        em.flush();
        level.setDependsOn(dependsOn);
        levelRepository.saveAndFlush(level);
        Long dependsOnId = dependsOn.getId();

        // Get all the levelList where dependsOn equals to dependsOnId
        defaultLevelShouldBeFound("dependsOnId.equals=" + dependsOnId);

        // Get all the levelList where dependsOn equals to dependsOnId + 1
        defaultLevelShouldNotBeFound("dependsOnId.equals=" + (dependsOnId + 1));
    }


    @Test
    @Transactional
    public void getAllLevelsBySkillsIsEqualToSomething() throws Exception {
        // Initialize the database
        LevelSkill skills = LevelSkillResourceIntTest.createEntity(em);
        em.persist(skills);
        em.flush();
        level.addSkills(skills);
        levelRepository.saveAndFlush(level);
        Long skillsId = skills.getId();

        // Get all the levelList where skills equals to skillsId
        defaultLevelShouldBeFound("skillsId.equals=" + skillsId);

        // Get all the levelList where skills equals to skillsId + 1
        defaultLevelShouldNotBeFound("skillsId.equals=" + (skillsId + 1));
    }

    @Test
    @Transactional
    public void getAllLevelsByImageIsEqualToSomething() throws Exception {
        // Initialize the database
        Image image = ImageResourceIntTest.createEntity(em);
        em.persist(image);
        em.flush();
        level.setImage(image);
        levelRepository.saveAndFlush(level);
        Long imageId = image.getId();

        // Get all the levelList where image equals to imageId
        defaultLevelShouldBeFound("imageId.equals=" + imageId);

        // Get all the levelList where image equals to imageId + 1
        defaultLevelShouldNotBeFound("imageId.equals=" + (imageId + 1));
    }

    @Test
    @Transactional
    public void getAllLevelsBySkillId() throws Exception {

        setupTestData();
        em.flush();

        restLevelMockMvc.perform(get("/api/levels?skillsId.in="+softwareUpdates.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$.[*].id").value(containsInAnyOrder(
                yellow.getId().intValue(),
                os1.getId().intValue())));


        // Initialize the database
        LevelSkill skills = LevelSkillResourceIntTest.createEntity(em);
        em.persist(skills);
        em.flush();
        level.addSkills(skills);
        levelRepository.saveAndFlush(level);
        Long skillsId = skills.getId();

        // Get all the levelList where skills equals to skillsId
        defaultLevelShouldBeFound("skillsId.equals=" + skillsId);

        // Get all the levelList where skills equals to skillsId + 1
        defaultLevelShouldNotBeFound("skillsId.equals=" + (skillsId + 1));
    }


    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultLevelShouldBeFound(String filter) throws Exception {
        restLevelMockMvc.perform(get("/api/levels?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(level.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].requiredScore").value(hasItem(DEFAULT_REQUIRED_SCORE.doubleValue())))
            .andExpect(jsonPath("$.[*].instantMultiplier").value(hasItem(DEFAULT_INSTANT_MULTIPLIER.doubleValue())))
            .andExpect(jsonPath("$.[*].completionBonus").value(hasItem(DEFAULT_COMPLETION_BONUS)));

        // Check, that the count call also returns 1
        restLevelMockMvc.perform(get("/api/levels/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultLevelShouldNotBeFound(String filter) throws Exception {
        restLevelMockMvc.perform(get("/api/levels?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLevelMockMvc.perform(get("/api/levels/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingLevel() throws Exception {
        // Get the level
        restLevelMockMvc.perform(get("/api/levels/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLevel() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        int databaseSizeBeforeUpdate = levelRepository.findAll().size();

        // Update the level
        Level updatedLevel = levelRepository.findById(level.getId()).get();
        // Disconnect from session so that the updates on updatedLevel are not directly saved in db
        em.detach(updatedLevel);
        updatedLevel
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .requiredScore(UPDATED_REQUIRED_SCORE)
            .instantMultiplier(UPDATED_INSTANT_MULTIPLIER)
            .completionBonus(UPDATED_COMPLETION_BONUS);
        LevelDTO levelDTO = levelMapper.toDto(updatedLevel);

        restLevelMockMvc.perform(put("/api/levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isOk());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
        Level testLevel = levelList.get(levelList.size() - 1);
        assertThat(testLevel.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLevel.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLevel.getRequiredScore()).isEqualTo(UPDATED_REQUIRED_SCORE);
        assertThat(testLevel.getInstantMultiplier()).isEqualTo(UPDATED_INSTANT_MULTIPLIER);
        assertThat(testLevel.getCompletionBonus()).isEqualTo(UPDATED_COMPLETION_BONUS);
    }

    @Test
    @Transactional
    public void updateNonExistingLevel() throws Exception {
        int databaseSizeBeforeUpdate = levelRepository.findAll().size();

        // Create the Level
        LevelDTO levelDTO = levelMapper.toDto(level);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLevelMockMvc.perform(put("/api/levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(levelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Level in the database
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLevel() throws Exception {
        // Initialize the database
        levelRepository.saveAndFlush(level);

        int databaseSizeBeforeDelete = levelRepository.findAll().size();

        // Delete the level
        restLevelMockMvc.perform(delete("/api/levels/{id}", level.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Level> levelList = levelRepository.findAll();
        assertThat(levelList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Level.class);
        Level level1 = new Level();
        level1.setId(1L);
        Level level2 = new Level();
        level2.setId(level1.getId());
        assertThat(level1).isEqualTo(level2);
        level2.setId(2L);
        assertThat(level1).isNotEqualTo(level2);
        level1.setId(null);
        assertThat(level1).isNotEqualTo(level2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LevelDTO.class);
        LevelDTO levelDTO1 = new LevelDTO();
        levelDTO1.setId(1L);
        LevelDTO levelDTO2 = new LevelDTO();
        assertThat(levelDTO1).isNotEqualTo(levelDTO2);
        levelDTO2.setId(levelDTO1.getId());
        assertThat(levelDTO1).isEqualTo(levelDTO2);
        levelDTO2.setId(2L);
        assertThat(levelDTO1).isNotEqualTo(levelDTO2);
        levelDTO1.setId(null);
        assertThat(levelDTO1).isNotEqualTo(levelDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(levelMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(levelMapper.fromId(null)).isNull();
    }

    private void setupTestData() {
        inputValidation = inputValidation().build(em);
        softwareUpdates = softwareUpdates().build(em);
        strongPasswords = strongPasswords().build(em);
        dockerized = dockerized().build(em);
        Skill evilUserStories = evilUserStories().build(em);

        security = security().build(em);
        operations = operations().build(em);

        yellow = yellow(security).addSkill(inputValidation).addSkill(softwareUpdates).build(em);
        orange = orange(security).addSkill(strongPasswords).dependsOn(yellow).build(em);
        os1 = os1(operations).addSkill(softwareUpdates).build(em);

        awsReady = awsReady().addDimension(security).addDimension(operations).
            addSkill(inputValidation).addSkill(dockerized).build(em);

        alwaysUpToDate = alwaysUpToDate().addSkill(softwareUpdates).build(em);

        team1 = ft1().build(em);
        team1.addParticipations(security);
        em.persist(team1);
        teamSkill = new TeamSkill();
        teamSkill.setTeam(team1);
        teamSkill.setSkill(inputValidation);
        teamSkill.setVote(1);
        em.persist(teamSkill);
        team1.addSkills(teamSkill);
        em.persist(team1);

        teamSkill = new TeamSkill();
        teamSkill.setTeam(team1);
        teamSkill.setSkill(softwareUpdates);
        teamSkill.setVote(1);
        em.persist(teamSkill);
        team1.addSkills(teamSkill);
        em.persist(team1);

        team2 = ft2().build(em);
        team2.addParticipations(security);
        team2.addParticipations(operations);
        em.persist(team2);
        teamSkill = new TeamSkill();
        teamSkill.setTeam(team2);
        teamSkill.setSkill(softwareUpdates);
        teamSkill.setCompletedAt(new Date().toInstant());
        teamSkill.setVote(1);
        em.persist(teamSkill);
        team2.addSkills(teamSkill);
        em.persist(team2);

    }
}
