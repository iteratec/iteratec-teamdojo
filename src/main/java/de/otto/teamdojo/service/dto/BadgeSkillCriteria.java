package de.otto.teamdojo.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the BadgeSkill entity. This class is used in BadgeSkillResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /badge-skills?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BadgeSkillCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter badgeId;

    private LongFilter skillId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(LongFilter badgeId) {
        this.badgeId = badgeId;
    }

    public LongFilter getSkillId() {
        return skillId;
    }

    public void setSkillId(LongFilter skillId) {
        this.skillId = skillId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BadgeSkillCriteria that = (BadgeSkillCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(badgeId, that.badgeId) &&
            Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        badgeId,
        skillId
        );
    }

    @Override
    public String toString() {
        return "BadgeSkillCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (badgeId != null ? "badgeId=" + badgeId + ", " : "") +
            (skillId != null ? "skillId=" + skillId + ", " : "") +
            "}";
    }

}
