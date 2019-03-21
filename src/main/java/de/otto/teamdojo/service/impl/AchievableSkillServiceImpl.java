package de.otto.teamdojo.service.impl;

import com.google.common.collect.Lists;
import de.otto.teamdojo.domain.Badge;
import de.otto.teamdojo.domain.Level;
import de.otto.teamdojo.domain.Team;
import de.otto.teamdojo.domain.enumeration.UserMode;
import de.otto.teamdojo.repository.BadgeRepository;
import de.otto.teamdojo.repository.SkillRepository;
import de.otto.teamdojo.repository.TeamRepository;
import de.otto.teamdojo.service.AchievableSkillService;
import de.otto.teamdojo.service.ActivityService;
import de.otto.teamdojo.service.OrganizationService;
import de.otto.teamdojo.service.TeamSkillService;
import de.otto.teamdojo.service.dto.AchievableSkillDTO;
import de.otto.teamdojo.service.dto.TeamSkillDTO;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class AchievableSkillServiceImpl implements AchievableSkillService {

    private final Logger log = LoggerFactory.getLogger(AchievableSkillServiceImpl.class);

    private static final List<String> ALL_FILTER = Lists.newArrayList("COMPLETE", "INCOMPLETE");

    private final SkillRepository skillRepository;

    private final TeamRepository teamRepository;

    private final BadgeRepository badgeRepository;

    private final TeamSkillService teamSkillService;

    private final ActivityService activityService;

    private final OrganizationService organizationService;


    public AchievableSkillServiceImpl(SkillRepository skillRepository,
                                      TeamRepository teamRepository,
                                      BadgeRepository badgeRepository,
                                      TeamSkillService teamSkillService,
                                      ActivityService activityService,
                                      OrganizationService organizationService) {
        this.skillRepository = skillRepository;
        this.teamRepository = teamRepository;
        this.badgeRepository = badgeRepository;
        this.teamSkillService = teamSkillService;
        this.activityService = activityService;
        this.organizationService = organizationService;
    }

    @Override
    public Page<AchievableSkillDTO> findAllByTeamAndLevelAndBadge(Long teamId, List<Long> levelIds, List<Long> badgeIds, List<String> filter, Pageable pageable) {
        List<String> queryFilter = getQueryFilter(filter);
        return levelIds.isEmpty() && badgeIds.isEmpty()
            ? findAllTeamRelated(teamId, queryFilter, pageable)
            : queryRepository(teamId, levelIds, badgeIds, queryFilter, pageable);
    }

    private List<String> getQueryFilter(List<String> filter) {
        List<String> queryFilter = new ArrayList<>();
        if (filter.isEmpty()) {
            queryFilter.addAll(ALL_FILTER);
        } else {
            queryFilter.addAll(filter);
        }
        return queryFilter;
    }

    @Override
    public Page<AchievableSkillDTO> findAllByTeamAndDimension(Long teamId, List<Long> dimensionIds, List<String> filter, Pageable pageable){
        List<String> queryFilter = getQueryFilter(filter);
        return skillRepository.findAchievableSkillsByDimensions(teamId, dimensionIds, queryFilter, pageable);
    }

    @Override
    public AchievableSkillDTO updateAchievableSkill(Long teamId, AchievableSkillDTO achievableSkill) throws JSONException {
        AchievableSkillDTO originSkill = skillRepository.findAchievableSkill(teamId, achievableSkill.getSkillId());

        TeamSkillDTO teamSkill = new TeamSkillDTO();
        teamSkill.setId((achievableSkill.getTeamSkillId() != null) ? achievableSkill.getTeamSkillId() : originSkill.getTeamSkillId());
        teamSkill.setTeamId(teamId);
        teamSkill.setSkillId(achievableSkill.getSkillId());
        teamSkill.setCompletedAt(achievableSkill.getAchievedAt());
        teamSkill.setVerifiedAt(achievableSkill.getVerifiedAt());
        teamSkill.setVote((achievableSkill.getVote() != null) ? achievableSkill.getVote() : 0);
        teamSkill.setVoters(achievableSkill.getVoters());
        teamSkill.setIrrelevant(achievableSkill.isIrrelevant());

        teamSkill = teamSkillService.save(teamSkill);

        if ((originSkill == null && teamSkill.getCompletedAt() != null) || (originSkill != null
            && originSkill.getAchievedAt() == null && teamSkill.getCompletedAt() != null)) {
            activityService.createForCompletedSkill(teamSkill);
        }

        if (organizationService.getCurrentOrganization().getUserMode().equals(UserMode.PERSON)
            && teamSkill.getCompletedAt() == null && teamSkill.getVote() == 1
            && (originSkill == null || !originSkill.getVote().equals(teamSkill.getVote()))) {
            activityService.createForSuggestedSkill(teamSkill);
        }

        return skillRepository.findAchievableSkill(teamId, achievableSkill.getSkillId());
    }

    public AchievableSkillDTO findAchievableSkill(Long teamId, Long skillId) {
        return skillRepository.findAchievableSkill(teamId, skillId);
    }

    private Page<AchievableSkillDTO> findAllTeamRelated(Long teamId, List<String> filter, Pageable pageable) {
        Team team = getTeam(teamId);
        List<Long> relatedLevelIds = getTeamRelatedLevelIds(team);
        List<Long> relatedBadgeIds = getTeamRelatedBadgeIds(team);
        relatedBadgeIds.addAll(getDimensionlessBadgeIds());
        return queryRepository(teamId, relatedLevelIds, relatedBadgeIds, filter, pageable);
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(NoSuchElementException::new);
    }

    private Page<AchievableSkillDTO> queryRepository(Long teamId, List<Long> levelIds, List<Long> badgeIds, List<String> filter, Pageable pageable) {

        if (!levelIds.isEmpty() && !badgeIds.isEmpty()) {
            return skillRepository.findAchievableSkillsByLevelsAndBadges(teamId, levelIds, badgeIds, filter, pageable);
        } else if (!levelIds.isEmpty()) {
            return skillRepository.findAchievableSkillsByLevels(teamId, levelIds, filter, pageable);
        } else if (!badgeIds.isEmpty()) {
            return skillRepository.findAchievableSkillsByBadges(teamId, badgeIds, filter, pageable);
        }
        return Page.empty();
    }

    private List<Long> getTeamRelatedLevelIds(Team team) {
        return team.getParticipations()
            .stream()
            .flatMap(dimension ->
                dimension.getLevels().stream().map(Level::getId))
            .collect(Collectors.toList());
    }

    private List<Long> getTeamRelatedBadgeIds(Team team) {
        return team.getParticipations()
            .stream()
            .flatMap(dimension ->
                dimension.getBadges().stream().map(Badge::getId))
            .distinct()
            .collect(Collectors.toList());
    }

    private List<Long> getDimensionlessBadgeIds() {
        return badgeRepository.findAllByDimensionsIsNull()
            .stream()
            .map(Badge::getId)
            .collect(Collectors.toList());
    }
}
