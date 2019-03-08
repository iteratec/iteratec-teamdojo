package de.otto.teamdojo.web.rest;
import de.otto.teamdojo.service.LevelSkillService;
import de.otto.teamdojo.web.rest.errors.BadRequestAlertException;
import de.otto.teamdojo.web.rest.util.HeaderUtil;
import de.otto.teamdojo.web.rest.util.PaginationUtil;
import de.otto.teamdojo.service.dto.LevelSkillDTO;
import de.otto.teamdojo.service.dto.LevelSkillCriteria;
import de.otto.teamdojo.service.LevelSkillQueryService;
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
 * REST controller for managing LevelSkill.
 */
@RestController
@RequestMapping("/api")
public class LevelSkillResource {

    private final Logger log = LoggerFactory.getLogger(LevelSkillResource.class);

    private static final String ENTITY_NAME = "levelSkill";

    private final LevelSkillService levelSkillService;

    private final LevelSkillQueryService levelSkillQueryService;

    public LevelSkillResource(LevelSkillService levelSkillService, LevelSkillQueryService levelSkillQueryService) {
        this.levelSkillService = levelSkillService;
        this.levelSkillQueryService = levelSkillQueryService;
    }

    /**
     * POST  /level-skills : Create a new levelSkill.
     *
     * @param levelSkillDTO the levelSkillDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new levelSkillDTO, or with status 400 (Bad Request) if the levelSkill has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/level-skills")
    public ResponseEntity<LevelSkillDTO> createLevelSkill(@Valid @RequestBody LevelSkillDTO levelSkillDTO) throws URISyntaxException {
        log.debug("REST request to save LevelSkill : {}", levelSkillDTO);
        if (levelSkillDTO.getId() != null) {
            throw new BadRequestAlertException("A new levelSkill cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LevelSkillDTO result = levelSkillService.save(levelSkillDTO);
        return ResponseEntity.created(new URI("/api/level-skills/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /level-skills : Updates an existing levelSkill.
     *
     * @param levelSkillDTO the levelSkillDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated levelSkillDTO,
     * or with status 400 (Bad Request) if the levelSkillDTO is not valid,
     * or with status 500 (Internal Server Error) if the levelSkillDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/level-skills")
    public ResponseEntity<LevelSkillDTO> updateLevelSkill(@Valid @RequestBody LevelSkillDTO levelSkillDTO) throws URISyntaxException {
        log.debug("REST request to update LevelSkill : {}", levelSkillDTO);
        if (levelSkillDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LevelSkillDTO result = levelSkillService.save(levelSkillDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, levelSkillDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /level-skills : get all the levelSkills.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of levelSkills in body
     */
    @GetMapping("/level-skills")
    public ResponseEntity<List<LevelSkillDTO>> getAllLevelSkills(LevelSkillCriteria criteria, Pageable pageable) {
        log.debug("REST request to get LevelSkills by criteria: {}", criteria);
        Page<LevelSkillDTO> page = levelSkillQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/level-skills");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
    * GET  /level-skills/count : count all the levelSkills.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/level-skills/count")
    public ResponseEntity<Long> countLevelSkills(LevelSkillCriteria criteria) {
        log.debug("REST request to count LevelSkills by criteria: {}", criteria);
        return ResponseEntity.ok().body(levelSkillQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /level-skills/:id : get the "id" levelSkill.
     *
     * @param id the id of the levelSkillDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the levelSkillDTO, or with status 404 (Not Found)
     */
    @GetMapping("/level-skills/{id}")
    public ResponseEntity<LevelSkillDTO> getLevelSkill(@PathVariable Long id) {
        log.debug("REST request to get LevelSkill : {}", id);
        Optional<LevelSkillDTO> levelSkillDTO = levelSkillService.findOne(id);
        return ResponseUtil.wrapOrNotFound(levelSkillDTO);
    }

    /**
     * DELETE  /level-skills/:id : delete the "id" levelSkill.
     *
     * @param id the id of the levelSkillDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/level-skills/{id}")
    public ResponseEntity<Void> deleteLevelSkill(@PathVariable Long id) {
        log.debug("REST request to delete LevelSkill : {}", id);
        levelSkillService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
