package de.otto.teamdojo.web.rest;
import de.otto.teamdojo.service.TeamSkillService;
import de.otto.teamdojo.web.rest.errors.BadRequestAlertException;
import de.otto.teamdojo.web.rest.util.HeaderUtil;
import de.otto.teamdojo.web.rest.util.PaginationUtil;
import de.otto.teamdojo.service.dto.TeamSkillDTO;
import de.otto.teamdojo.service.dto.TeamSkillCriteria;
import de.otto.teamdojo.service.TeamSkillQueryService;
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
 * REST controller for managing TeamSkill.
 */
@RestController
@RequestMapping("/api")
public class TeamSkillResource {

    private final Logger log = LoggerFactory.getLogger(TeamSkillResource.class);

    private static final String ENTITY_NAME = "teamSkill";

    private final TeamSkillService teamSkillService;

    private final TeamSkillQueryService teamSkillQueryService;

    public TeamSkillResource(TeamSkillService teamSkillService, TeamSkillQueryService teamSkillQueryService) {
        this.teamSkillService = teamSkillService;
        this.teamSkillQueryService = teamSkillQueryService;
    }

    /**
     * POST  /team-skills : Create a new teamSkill.
     *
     * @param teamSkillDTO the teamSkillDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new teamSkillDTO, or with status 400 (Bad Request) if the teamSkill has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/team-skills")
    public ResponseEntity<TeamSkillDTO> createTeamSkill(@Valid @RequestBody TeamSkillDTO teamSkillDTO) throws URISyntaxException {
        log.debug("REST request to save TeamSkill : {}", teamSkillDTO);
        if (teamSkillDTO.getId() != null) {
            throw new BadRequestAlertException("A new teamSkill cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TeamSkillDTO result = teamSkillService.save(teamSkillDTO);
        return ResponseEntity.created(new URI("/api/team-skills/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /team-skills : Updates an existing teamSkill.
     *
     * @param teamSkillDTO the teamSkillDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated teamSkillDTO,
     * or with status 400 (Bad Request) if the teamSkillDTO is not valid,
     * or with status 500 (Internal Server Error) if the teamSkillDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/team-skills")
    public ResponseEntity<TeamSkillDTO> updateTeamSkill(@Valid @RequestBody TeamSkillDTO teamSkillDTO) throws URISyntaxException {
        log.debug("REST request to update TeamSkill : {}", teamSkillDTO);
        if (teamSkillDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TeamSkillDTO result = teamSkillService.save(teamSkillDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, teamSkillDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /team-skills : get all the teamSkills.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of teamSkills in body
     */
    @GetMapping("/team-skills")
    public ResponseEntity<List<TeamSkillDTO>> getAllTeamSkills(TeamSkillCriteria criteria, Pageable pageable) {
        log.debug("REST request to get TeamSkills by criteria: {}", criteria);
        Page<TeamSkillDTO> page = teamSkillQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/team-skills");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /team-skills/count : count all the teamSkills.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/team-skills/count")
    public ResponseEntity<Long> countTeamSkills(TeamSkillCriteria criteria) {
        log.debug("REST request to count TeamSkills by criteria: {}", criteria);
        return ResponseEntity.ok().body(teamSkillQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /team-skills/:id : get the "id" teamSkill.
     *
     * @param id the id of the teamSkillDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the teamSkillDTO, or with status 404 (Not Found)
     */
    @GetMapping("/team-skills/{id}")
    public ResponseEntity<TeamSkillDTO> getTeamSkill(@PathVariable Long id) {
        log.debug("REST request to get TeamSkill : {}", id);
        Optional<TeamSkillDTO> teamSkillDTO = teamSkillService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teamSkillDTO);
    }

    /**
     * DELETE  /team-skills/:id : delete the "id" teamSkill.
     *
     * @param id the id of the teamSkillDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/team-skills/{id}")
    public ResponseEntity<Void> deleteTeamSkill(@PathVariable Long id) {
        log.debug("REST request to delete TeamSkill : {}", id);
        teamSkillService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
