package de.otto.teamdojo.web.rest;

import de.otto.teamdojo.TeamdojoApp;
import de.otto.teamdojo.domain.*;
import de.otto.teamdojo.repository.BadgeRepository;
import de.otto.teamdojo.service.ActivityService;
import de.otto.teamdojo.service.BadgeQueryService;
import de.otto.teamdojo.service.BadgeService;
import de.otto.teamdojo.service.BadgeSkillService;
import de.otto.teamdojo.service.dto.BadgeDTO;
import de.otto.teamdojo.service.impl.BadgeServiceImpl;
import de.otto.teamdojo.service.mapper.BadgeMapper;
import de.otto.teamdojo.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import static de.otto.teamdojo.test.util.BadgeTestDataProvider.alwaysUpToDate;
import static de.otto.teamdojo.test.util.BadgeTestDataProvider.awsReady;
import static de.otto.teamdojo.test.util.DimensionTestDataProvider.security;
import static de.otto.teamdojo.test.util.DimensionTestDataProvider.operations;
import static de.otto.teamdojo.test.util.LevelTestDataProvider.orange;
import static de.otto.teamdojo.test.util.LevelTestDataProvider.yellow;
import static de.otto.teamdojo.test.util.LevelTestDataProvider.os1;
import static de.otto.teamdojo.test.util.SkillTestDataProvider.*;
import static de.otto.teamdojo.test.util.SkillTestDataProvider.evilUserStories;
import static de.otto.teamdojo.test.util.TeamTestDataProvider.ft1;
import static de.otto.teamdojo.test.util.TeamTestDataProvider.ft2;
import static de.otto.teamdojo.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BadgeResource REST controller.
 *
 * @see BadgeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TeamdojoApp.class)
public class BadgeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_AVAILABLE_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_UNTIL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_AVAILABLE_AMOUNT = 1;
    private static final Integer UPDATED_AVAILABLE_AMOUNT = 2;

    private static final Double DEFAULT_REQUIRED_SCORE = 0D;
    private static final Double UPDATED_REQUIRED_SCORE = 1D;

    private static final Double DEFAULT_INSTANT_MULTIPLIER = 0D;
    private static final Double UPDATED_INSTANT_MULTIPLIER = 1D;

    private static final Integer DEFAULT_COMPLETION_BONUS = 0;
    private static final Integer UPDATED_COMPLETION_BONUS = 1;

    @Autowired
    private BadgeRepository badgeRepository;

    @Mock
    private ActivityService activityServiceMock;

    @Autowired
    private BadgeMapper badgeMapper;

    @Mock
    private BadgeService badgeServiceMock;

    @Autowired
    private BadgeQueryService badgeQueryService;

    @Autowired
    private BadgeSkillService badgeSkillService;

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

    private MockMvc restBadgeMockMvc;

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
        BadgeService badgeService = new BadgeServiceImpl(badgeRepository, badgeMapper, activityServiceMock);
        final BadgeResource badgeResource = new BadgeResource(badgeService, badgeQueryService, badgeSkillService);
        this.restBadgeMockMvc = MockMvcBuilders.standaloneSetup(badgeResource)
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
    public static Badge createEntity(EntityManager em) {
        Badge badge = new Badge()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .availableUntil(DEFAULT_AVAILABLE_UNTIL)
            .availableAmount(DEFAULT_AVAILABLE_AMOUNT)
            .requiredScore(DEFAULT_REQUIRED_SCORE)
            .instantMultiplier(DEFAULT_INSTANT_MULTIPLIER)
            .completionBonus(DEFAULT_COMPLETION_BONUS);
        return badge;
    }

    @Before
    public void initTest() {
        badge = createEntity(em);
    }

    @Test
    @Transactional
    public void createBadge() throws Exception {
        int databaseSizeBeforeCreate = badgeRepository.findAll().size();

        // Create the Badge
        BadgeDTO badgeDTO = badgeMapper.toDto(badge);
        restBadgeMockMvc.perform(post("/api/badges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(badgeDTO)))
            .andExpect(status().isCreated());

        // Validate the Badge in the database
        List<Badge> badgeList = badgeRepository.findAll();
        assertThat(badgeList).hasSize(databaseSizeBeforeCreate + 1);
        Badge testBadge = badgeList.get(badgeList.size() - 1);
        assertThat(testBadge.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBadge.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBadge.getAvailableUntil()).isEqualTo(DEFAULT_AVAILABLE_UNTIL);
        assertThat(testBadge.getAvailableAmount()).isEqualTo(DEFAULT_AVAILABLE_AMOUNT);
        assertThat(testBadge.getRequiredScore()).isEqualTo(DEFAULT_REQUIRED_SCORE);
        assertThat(testBadge.getInstantMultiplier()).isEqualTo(DEFAULT_INSTANT_MULTIPLIER);
        assertThat(testBadge.getCompletionBonus()).isEqualTo(DEFAULT_COMPLETION_BONUS);
    }

    @Test
    @Transactional
    public void createBadgeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = badgeRepository.findAll().size();

        // Create the Badge with an existing ID
        badge.setId(1L);
        BadgeDTO badgeDTO = badgeMapper.toDto(badge);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBadgeMockMvc.perform(post("/api/badges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(badgeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Badge in the database
        List<Badge> badgeList = badgeRepository.findAll();
        assertThat(badgeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = badgeRepository.findAll().size();
        // set the field null
        badge.setName(null);

        // Create the Badge, which fails.
        BadgeDTO badgeDTO = badgeMapper.toDto(badge);

        restBadgeMockMvc.perform(post("/api/badges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(badgeDTO)))
            .andExpect(status().isBadRequest());

        List<Badge> badgeList = badgeRepository.findAll();
        assertThat(badgeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRequiredScoreIsRequired() throws Exception {
        int databaseSizeBeforeTest = badgeRepository.findAll().size();
        // set the field null
        badge.setRequiredScore(null);

        // Create the Badge, which fails.
        BadgeDTO badgeDTO = badgeMapper.toDto(badge);

        restBadgeMockMvc.perform(post("/api/badges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(badgeDTO)))
            .andExpect(status().isBadRequest());

        List<Badge> badgeList = badgeRepository.findAll();
        assertThat(badgeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkInstantMultiplierIsRequired() throws Exception {
        int databaseSizeBeforeTest = badgeRepository.findAll().size();
        // set the field null
        badge.setInstantMultiplier(null);

        // Create the Badge, which fails.
        BadgeDTO badgeDTO = badgeMapper.toDto(badge);

        restBadgeMockMvc.perform(post("/api/badges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(badgeDTO)))
            .andExpect(status().isBadRequest());

        List<Badge> badgeList = badgeRepository.findAll();
        assertThat(badgeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBadges() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList
        restBadgeMockMvc.perform(get("/api/badges?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(badge.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].availableUntil").value(hasItem(DEFAULT_AVAILABLE_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].availableAmount").value(hasItem(DEFAULT_AVAILABLE_AMOUNT)))
            .andExpect(jsonPath("$.[*].requiredScore").value(hasItem(DEFAULT_REQUIRED_SCORE.doubleValue())))
            .andExpect(jsonPath("$.[*].instantMultiplier").value(hasItem(DEFAULT_INSTANT_MULTIPLIER.doubleValue())))
            .andExpect(jsonPath("$.[*].completionBonus").value(hasItem(DEFAULT_COMPLETION_BONUS)));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllBadgesWithEagerRelationshipsIsEnabled() throws Exception {
        BadgeResource badgeResource = new BadgeResource(badgeServiceMock, badgeQueryService, badgeSkillService);
        when(badgeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restBadgeMockMvc = MockMvcBuilders.standaloneSetup(badgeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restBadgeMockMvc.perform(get("/api/badges?eagerload=true"))
            .andExpect(status().isOk());

        verify(badgeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllBadgesWithEagerRelationshipsIsNotEnabled() throws Exception {
        BadgeResource badgeResource = new BadgeResource(badgeServiceMock, badgeQueryService, badgeSkillService);
        when(badgeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
        MockMvc restBadgeMockMvc = MockMvcBuilders.standaloneSetup(badgeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restBadgeMockMvc.perform(get("/api/badges?eagerload=true"))
            .andExpect(status().isOk());

        verify(badgeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getBadge() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get the badge
        restBadgeMockMvc.perform(get("/api/badges/{id}", badge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(badge.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.availableUntil").value(DEFAULT_AVAILABLE_UNTIL.toString()))
            .andExpect(jsonPath("$.availableAmount").value(DEFAULT_AVAILABLE_AMOUNT))
            .andExpect(jsonPath("$.requiredScore").value(DEFAULT_REQUIRED_SCORE.doubleValue()))
            .andExpect(jsonPath("$.instantMultiplier").value(DEFAULT_INSTANT_MULTIPLIER.doubleValue()))
            .andExpect(jsonPath("$.completionBonus").value(DEFAULT_COMPLETION_BONUS));
    }

    @Test
    @Transactional
    public void getAllBadgesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where name equals to DEFAULT_NAME
        defaultBadgeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the badgeList where name equals to UPDATED_NAME
        defaultBadgeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBadgesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBadgeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the badgeList where name equals to UPDATED_NAME
        defaultBadgeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBadgesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where name is not null
        defaultBadgeShouldBeFound("name.specified=true");

        // Get all the badgeList where name is null
        defaultBadgeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllBadgesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where description equals to DEFAULT_DESCRIPTION
        defaultBadgeShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the badgeList where description equals to UPDATED_DESCRIPTION
        defaultBadgeShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllBadgesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultBadgeShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the badgeList where description equals to UPDATED_DESCRIPTION
        defaultBadgeShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllBadgesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where description is not null
        defaultBadgeShouldBeFound("description.specified=true");

        // Get all the badgeList where description is null
        defaultBadgeShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllBadgesByAvailableUntilIsEqualToSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where availableUntil equals to DEFAULT_AVAILABLE_UNTIL
        defaultBadgeShouldBeFound("availableUntil.equals=" + DEFAULT_AVAILABLE_UNTIL);

        // Get all the badgeList where availableUntil equals to UPDATED_AVAILABLE_UNTIL
        defaultBadgeShouldNotBeFound("availableUntil.equals=" + UPDATED_AVAILABLE_UNTIL);
    }

    @Test
    @Transactional
    public void getAllBadgesByAvailableUntilIsInShouldWork() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where availableUntil in DEFAULT_AVAILABLE_UNTIL or UPDATED_AVAILABLE_UNTIL
        defaultBadgeShouldBeFound("availableUntil.in=" + DEFAULT_AVAILABLE_UNTIL + "," + UPDATED_AVAILABLE_UNTIL);

        // Get all the badgeList where availableUntil equals to UPDATED_AVAILABLE_UNTIL
        defaultBadgeShouldNotBeFound("availableUntil.in=" + UPDATED_AVAILABLE_UNTIL);
    }

    @Test
    @Transactional
    public void getAllBadgesByAvailableUntilIsNullOrNotNull() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where availableUntil is not null
        defaultBadgeShouldBeFound("availableUntil.specified=true");

        // Get all the badgeList where availableUntil is null
        defaultBadgeShouldNotBeFound("availableUntil.specified=false");
    }

    @Test
    @Transactional
    public void getAllBadgesByAvailableAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where availableAmount equals to DEFAULT_AVAILABLE_AMOUNT
        defaultBadgeShouldBeFound("availableAmount.equals=" + DEFAULT_AVAILABLE_AMOUNT);

        // Get all the badgeList where availableAmount equals to UPDATED_AVAILABLE_AMOUNT
        defaultBadgeShouldNotBeFound("availableAmount.equals=" + UPDATED_AVAILABLE_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllBadgesByAvailableAmountIsInShouldWork() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where availableAmount in DEFAULT_AVAILABLE_AMOUNT or UPDATED_AVAILABLE_AMOUNT
        defaultBadgeShouldBeFound("availableAmount.in=" + DEFAULT_AVAILABLE_AMOUNT + "," + UPDATED_AVAILABLE_AMOUNT);

        // Get all the badgeList where availableAmount equals to UPDATED_AVAILABLE_AMOUNT
        defaultBadgeShouldNotBeFound("availableAmount.in=" + UPDATED_AVAILABLE_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllBadgesByAvailableAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where availableAmount is not null
        defaultBadgeShouldBeFound("availableAmount.specified=true");

        // Get all the badgeList where availableAmount is null
        defaultBadgeShouldNotBeFound("availableAmount.specified=false");
    }

    @Test
    @Transactional
    public void getAllBadgesByAvailableAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where availableAmount greater than or equals to DEFAULT_AVAILABLE_AMOUNT
        defaultBadgeShouldBeFound("availableAmount.greaterOrEqualThan=" + DEFAULT_AVAILABLE_AMOUNT);

        // Get all the badgeList where availableAmount greater than or equals to UPDATED_AVAILABLE_AMOUNT
        defaultBadgeShouldNotBeFound("availableAmount.greaterOrEqualThan=" + UPDATED_AVAILABLE_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllBadgesByAvailableAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where availableAmount less than or equals to DEFAULT_AVAILABLE_AMOUNT
        defaultBadgeShouldNotBeFound("availableAmount.lessThan=" + DEFAULT_AVAILABLE_AMOUNT);

        // Get all the badgeList where availableAmount less than or equals to UPDATED_AVAILABLE_AMOUNT
        defaultBadgeShouldBeFound("availableAmount.lessThan=" + UPDATED_AVAILABLE_AMOUNT);
    }


    @Test
    @Transactional
    public void getAllBadgesByRequiredScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where requiredScore equals to DEFAULT_REQUIRED_SCORE
        defaultBadgeShouldBeFound("requiredScore.equals=" + DEFAULT_REQUIRED_SCORE);

        // Get all the badgeList where requiredScore equals to UPDATED_REQUIRED_SCORE
        defaultBadgeShouldNotBeFound("requiredScore.equals=" + UPDATED_REQUIRED_SCORE);
    }

    @Test
    @Transactional
    public void getAllBadgesByRequiredScoreIsInShouldWork() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where requiredScore in DEFAULT_REQUIRED_SCORE or UPDATED_REQUIRED_SCORE
        defaultBadgeShouldBeFound("requiredScore.in=" + DEFAULT_REQUIRED_SCORE + "," + UPDATED_REQUIRED_SCORE);

        // Get all the badgeList where requiredScore equals to UPDATED_REQUIRED_SCORE
        defaultBadgeShouldNotBeFound("requiredScore.in=" + UPDATED_REQUIRED_SCORE);
    }

    @Test
    @Transactional
    public void getAllBadgesByRequiredScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where requiredScore is not null
        defaultBadgeShouldBeFound("requiredScore.specified=true");

        // Get all the badgeList where requiredScore is null
        defaultBadgeShouldNotBeFound("requiredScore.specified=false");
    }

    @Test
    @Transactional
    public void getAllBadgesByInstantMultiplierIsEqualToSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where instantMultiplier equals to DEFAULT_INSTANT_MULTIPLIER
        defaultBadgeShouldBeFound("instantMultiplier.equals=" + DEFAULT_INSTANT_MULTIPLIER);

        // Get all the badgeList where instantMultiplier equals to UPDATED_INSTANT_MULTIPLIER
        defaultBadgeShouldNotBeFound("instantMultiplier.equals=" + UPDATED_INSTANT_MULTIPLIER);
    }

    @Test
    @Transactional
    public void getAllBadgesByInstantMultiplierIsInShouldWork() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where instantMultiplier in DEFAULT_INSTANT_MULTIPLIER or UPDATED_INSTANT_MULTIPLIER
        defaultBadgeShouldBeFound("instantMultiplier.in=" + DEFAULT_INSTANT_MULTIPLIER + "," + UPDATED_INSTANT_MULTIPLIER);

        // Get all the badgeList where instantMultiplier equals to UPDATED_INSTANT_MULTIPLIER
        defaultBadgeShouldNotBeFound("instantMultiplier.in=" + UPDATED_INSTANT_MULTIPLIER);
    }

    @Test
    @Transactional
    public void getAllBadgesByInstantMultiplierIsNullOrNotNull() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where instantMultiplier is not null
        defaultBadgeShouldBeFound("instantMultiplier.specified=true");

        // Get all the badgeList where instantMultiplier is null
        defaultBadgeShouldNotBeFound("instantMultiplier.specified=false");
    }

    @Test
    @Transactional
    public void getAllBadgesByCompletionBonusIsEqualToSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where completionBonus equals to DEFAULT_COMPLETION_BONUS
        defaultBadgeShouldBeFound("completionBonus.equals=" + DEFAULT_COMPLETION_BONUS);

        // Get all the badgeList where completionBonus equals to UPDATED_COMPLETION_BONUS
        defaultBadgeShouldNotBeFound("completionBonus.equals=" + UPDATED_COMPLETION_BONUS);
    }

    @Test
    @Transactional
    public void getAllBadgesByCompletionBonusIsInShouldWork() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where completionBonus in DEFAULT_COMPLETION_BONUS or UPDATED_COMPLETION_BONUS
        defaultBadgeShouldBeFound("completionBonus.in=" + DEFAULT_COMPLETION_BONUS + "," + UPDATED_COMPLETION_BONUS);

        // Get all the badgeList where completionBonus equals to UPDATED_COMPLETION_BONUS
        defaultBadgeShouldNotBeFound("completionBonus.in=" + UPDATED_COMPLETION_BONUS);
    }

    @Test
    @Transactional
    public void getAllBadgesByCompletionBonusIsNullOrNotNull() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where completionBonus is not null
        defaultBadgeShouldBeFound("completionBonus.specified=true");

        // Get all the badgeList where completionBonus is null
        defaultBadgeShouldNotBeFound("completionBonus.specified=false");
    }

    @Test
    @Transactional
    public void getAllBadgesByCompletionBonusIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where completionBonus greater than or equals to DEFAULT_COMPLETION_BONUS
        defaultBadgeShouldBeFound("completionBonus.greaterOrEqualThan=" + DEFAULT_COMPLETION_BONUS);

        // Get all the badgeList where completionBonus greater than or equals to UPDATED_COMPLETION_BONUS
        defaultBadgeShouldNotBeFound("completionBonus.greaterOrEqualThan=" + UPDATED_COMPLETION_BONUS);
    }

    @Test
    @Transactional
    public void getAllBadgesByCompletionBonusIsLessThanSomething() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        // Get all the badgeList where completionBonus less than or equals to DEFAULT_COMPLETION_BONUS
        defaultBadgeShouldNotBeFound("completionBonus.lessThan=" + DEFAULT_COMPLETION_BONUS);

        // Get all the badgeList where completionBonus less than or equals to UPDATED_COMPLETION_BONUS
        defaultBadgeShouldBeFound("completionBonus.lessThan=" + UPDATED_COMPLETION_BONUS);
    }


    @Test
    @Transactional
    public void getAllBadgesBySkillsIsEqualToSomething() throws Exception {
        // Initialize the database
        BadgeSkill skills = BadgeSkillResourceIntTest.createEntity(em);
        em.persist(skills);
        em.flush();
        badge.addSkills(skills);
        badgeRepository.saveAndFlush(badge);
        Long skillsId = skills.getId();

        // Get all the badgeList where skills equals to skillsId
        defaultBadgeShouldBeFound("skillsId.equals=" + skillsId);

        // Get all the badgeList where skills equals to skillsId + 1
        defaultBadgeShouldNotBeFound("skillsId.equals=" + (skillsId + 1));
    }

    @Test
    @Transactional
    public void getAllBadgesBySkillIds() throws Exception {

        setupTestData();
        em.flush();

        restBadgeMockMvc.perform(get("/api/badges?skillsId.in=" + softwareUpdates.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$.[*].id").value(containsInAnyOrder(alwaysUpToDate.getId().intValue())));

    }


    @Test
    @Transactional
    public void getAllBadgesByDimensionsIsEqualToSomething() throws Exception {
        // Initialize the database
        Dimension dimensions = DimensionResourceIntTest.createEntity(em);
        em.persist(dimensions);
        em.flush();
        badge.addDimensions(dimensions);
        badgeRepository.saveAndFlush(badge);
        Long dimensionsId = dimensions.getId();

        // Get all the badgeList where dimensions equals to dimensionsId
        defaultBadgeShouldBeFound("dimensionsId.equals=" + dimensionsId);

        // Get all the badgeList where dimensions equals to dimensionsId + 1
        defaultBadgeShouldNotBeFound("dimensionsId.equals=" + (dimensionsId + 1));
    }


    @Test
    @Transactional
    public void getAllBadgesByImageIsEqualToSomething() throws Exception {
        // Initialize the database
        Image image = ImageResourceIntTest.createEntity(em);
        em.persist(image);
        em.flush();
        badge.setImage(image);
        badgeRepository.saveAndFlush(badge);
        Long imageId = image.getId();

        // Get all the badgeList where image equals to imageId
        defaultBadgeShouldBeFound("imageId.equals=" + imageId);

        // Get all the badgeList where image equals to imageId + 1
        defaultBadgeShouldNotBeFound("imageId.equals=" + (imageId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultBadgeShouldBeFound(String filter) throws Exception {
        restBadgeMockMvc.perform(get("/api/badges?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(badge.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].availableUntil").value(hasItem(DEFAULT_AVAILABLE_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].availableAmount").value(hasItem(DEFAULT_AVAILABLE_AMOUNT)))
            .andExpect(jsonPath("$.[*].requiredScore").value(hasItem(DEFAULT_REQUIRED_SCORE.doubleValue())))
            .andExpect(jsonPath("$.[*].instantMultiplier").value(hasItem(DEFAULT_INSTANT_MULTIPLIER.doubleValue())))
            .andExpect(jsonPath("$.[*].completionBonus").value(hasItem(DEFAULT_COMPLETION_BONUS)));

        // Check, that the count call also returns 1
        restBadgeMockMvc.perform(get("/api/badges/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultBadgeShouldNotBeFound(String filter) throws Exception {
        restBadgeMockMvc.perform(get("/api/badges?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBadgeMockMvc.perform(get("/api/badges/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingBadge() throws Exception {
        // Get the badge
        restBadgeMockMvc.perform(get("/api/badges/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBadge() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        int databaseSizeBeforeUpdate = badgeRepository.findAll().size();

        // Update the badge
        Badge updatedBadge = badgeRepository.findById(badge.getId()).get();
        // Disconnect from session so that the updates on updatedBadge are not directly saved in db
        em.detach(updatedBadge);
        updatedBadge
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .availableUntil(UPDATED_AVAILABLE_UNTIL)
            .availableAmount(UPDATED_AVAILABLE_AMOUNT)
            .requiredScore(UPDATED_REQUIRED_SCORE)
            .instantMultiplier(UPDATED_INSTANT_MULTIPLIER)
            .completionBonus(UPDATED_COMPLETION_BONUS);
        BadgeDTO badgeDTO = badgeMapper.toDto(updatedBadge);

        restBadgeMockMvc.perform(put("/api/badges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(badgeDTO)))
            .andExpect(status().isOk());

        // Validate the Badge in the database
        List<Badge> badgeList = badgeRepository.findAll();
        assertThat(badgeList).hasSize(databaseSizeBeforeUpdate);
        Badge testBadge = badgeList.get(badgeList.size() - 1);
        assertThat(testBadge.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBadge.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBadge.getAvailableUntil()).isEqualTo(UPDATED_AVAILABLE_UNTIL);
        assertThat(testBadge.getAvailableAmount()).isEqualTo(UPDATED_AVAILABLE_AMOUNT);
        assertThat(testBadge.getRequiredScore()).isEqualTo(UPDATED_REQUIRED_SCORE);
        assertThat(testBadge.getInstantMultiplier()).isEqualTo(UPDATED_INSTANT_MULTIPLIER);
        assertThat(testBadge.getCompletionBonus()).isEqualTo(UPDATED_COMPLETION_BONUS);
    }

    @Test
    @Transactional
    public void updateNonExistingBadge() throws Exception {
        int databaseSizeBeforeUpdate = badgeRepository.findAll().size();

        // Create the Badge
        BadgeDTO badgeDTO = badgeMapper.toDto(badge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBadgeMockMvc.perform(put("/api/badges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(badgeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Badge in the database
        List<Badge> badgeList = badgeRepository.findAll();
        assertThat(badgeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBadge() throws Exception {
        // Initialize the database
        badgeRepository.saveAndFlush(badge);

        int databaseSizeBeforeDelete = badgeRepository.findAll().size();

        // Delete the badge
        restBadgeMockMvc.perform(delete("/api/badges/{id}", badge.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Badge> badgeList = badgeRepository.findAll();
        assertThat(badgeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Badge.class);
        Badge badge1 = new Badge();
        badge1.setId(1L);
        Badge badge2 = new Badge();
        badge2.setId(badge1.getId());
        assertThat(badge1).isEqualTo(badge2);
        badge2.setId(2L);
        assertThat(badge1).isNotEqualTo(badge2);
        badge1.setId(null);
        assertThat(badge1).isNotEqualTo(badge2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BadgeDTO.class);
        BadgeDTO badgeDTO1 = new BadgeDTO();
        badgeDTO1.setId(1L);
        BadgeDTO badgeDTO2 = new BadgeDTO();
        assertThat(badgeDTO1).isNotEqualTo(badgeDTO2);
        badgeDTO2.setId(badgeDTO1.getId());
        assertThat(badgeDTO1).isEqualTo(badgeDTO2);
        badgeDTO2.setId(2L);
        assertThat(badgeDTO1).isNotEqualTo(badgeDTO2);
        badgeDTO1.setId(null);
        assertThat(badgeDTO1).isNotEqualTo(badgeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(badgeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(badgeMapper.fromId(null)).isNull();
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
