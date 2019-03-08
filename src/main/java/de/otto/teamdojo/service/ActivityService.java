package de.otto.teamdojo.service;

import de.otto.teamdojo.service.dto.ActivityDTO;
import de.otto.teamdojo.service.dto.BadgeDTO;
import de.otto.teamdojo.service.dto.TeamSkillDTO;
import org.json.JSONException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Activity.
 */
public interface ActivityService {

    /**
     * Save a activity.
     *
     * @param activityDTO the entity to save
     * @return the persisted entity
     */
    ActivityDTO save(ActivityDTO activityDTO);

    /**
     * Create an activity for BADGE_CREATED
     * @return the persisted entity
     */
    ActivityDTO createForNewBadge(BadgeDTO badgeDTO) throws JSONException;

    /**
     * Create an activity for SKILL_COMPLETED
     * @param teamSkill
     * @return the persisted entity
     */
    ActivityDTO createForCompletedSkill(TeamSkillDTO teamSkill) throws JSONException;

    /**
     * Create an activity for SKILL_SUGGESTED
     * @param teamSkill
     */
    void createForSuggestedSkill(TeamSkillDTO teamSkill) throws JSONException;

    /**
     * Get all the activities.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ActivityDTO> findAll(Pageable pageable);


    /**
     * Get the "id" activity.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<ActivityDTO> findOne(Long id);

    /**
     * Delete the "id" activity.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
