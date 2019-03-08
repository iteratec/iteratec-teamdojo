package de.otto.teamdojo.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import de.otto.teamdojo.domain.Badge;
import de.otto.teamdojo.domain.*; // for static metamodels
import de.otto.teamdojo.repository.BadgeRepository;
import de.otto.teamdojo.service.dto.BadgeCriteria;
import de.otto.teamdojo.service.dto.BadgeDTO;
import de.otto.teamdojo.service.mapper.BadgeMapper;

/**
 * Service for executing complex queries for Badge entities in the database.
 * The main input is a {@link BadgeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BadgeDTO} or a {@link Page} of {@link BadgeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BadgeQueryService extends QueryService<Badge> {

    private final Logger log = LoggerFactory.getLogger(BadgeQueryService.class);

    private final BadgeRepository badgeRepository;

    private final BadgeMapper badgeMapper;

    public BadgeQueryService(BadgeRepository badgeRepository, BadgeMapper badgeMapper) {
        this.badgeRepository = badgeRepository;
        this.badgeMapper = badgeMapper;
    }

    /**
     * Return a {@link List} of {@link BadgeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BadgeDTO> findByCriteria(BadgeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Badge> specification = createSpecification(criteria);
        return badgeMapper.toDto(badgeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BadgeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BadgeDTO> findByCriteria(BadgeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Badge> specification = createSpecification(criteria);
        return badgeRepository.findAll(specification, page)
            .map(badgeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BadgeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Badge> specification = createSpecification(criteria);
        return badgeRepository.count(specification);
    }

    /**
     * Function to convert BadgeCriteria to a {@link Specification}
     */
    private Specification<Badge> createSpecification(BadgeCriteria criteria) {
        Specification<Badge> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Badge_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Badge_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Badge_.description));
            }
            if (criteria.getAvailableUntil() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAvailableUntil(), Badge_.availableUntil));
            }
            if (criteria.getAvailableAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAvailableAmount(), Badge_.availableAmount));
            }
            if (criteria.getRequiredScore() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRequiredScore(), Badge_.requiredScore));
            }
            if (criteria.getInstantMultiplier() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getInstantMultiplier(), Badge_.instantMultiplier));
            }
            if (criteria.getCompletionBonus() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCompletionBonus(), Badge_.completionBonus));
            }
            if (criteria.getSkillsId() != null) {
                specification = specification.and(buildSpecification(criteria.getSkillsId(),
                    root -> root.join(Badge_.skills, JoinType.LEFT).get(BadgeSkill_.id)));
            }
            if (criteria.getDimensionsId() != null) {
                specification = specification.and(buildSpecification(criteria.getDimensionsId(),
                    root -> root.join(Badge_.dimensions, JoinType.LEFT).get(Dimension_.id)));
            }
            if (criteria.getImageId() != null) {
                specification = specification.and(buildSpecification(criteria.getImageId(),
                    root -> root.join(Badge_.image, JoinType.LEFT).get(Image_.id)));
            }
        }
        return specification;
    }
}
